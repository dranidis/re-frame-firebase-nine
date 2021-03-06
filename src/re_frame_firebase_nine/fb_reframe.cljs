(ns re-frame-firebase-nine.fb-reframe
  (:require [re-frame.core :as re-frame]
            [re-frame-firebase-nine.firebase-database :refer [set-value! default-set-success-callback default-set-error-callback on-value off push-value! update! connect-database-emulator get-db]]
            [reagent.ratom :as ratom]
            [re-frame.utils :refer [dissoc-in]]
            [re-frame-firebase-nine.firebase-auth :as firebase-auth :refer [error-callback sign-in sign-out create-user on-auth-state-changed connect-auth-emulator get-auth]]
            [re-frame-firebase-nine.firebase-app :refer [init-app]]
            [clojure.spec.alpha :as spec]
            [clojure.test :refer [is]]
            [re-frame-firebase-nine.utils :refer [if-vector?->map]]
            [re-frame.loggers :refer [console]]))


(def connected-to-emalator? (atom false))

;; Connect to emulator
;;
(defn connect-emulator
  "Connects to db and auth emulators when location is localhost"
  []
  (when (and (not @connected-to-emalator?) (= "localhost" (.-hostname js/location)))
    (connect-database-emulator (get-db) "localhost" 9000)
    (connect-auth-emulator (get-auth) "http://localhost:9099")
    (reset! connected-to-emalator? true)))


;; Effect for setting a value in firebase. Optional :success and :error keys for handlers
;; Data can be deleted by giving null as value
(re-frame/reg-fx
 ::firebase-set
 (fn [{:keys [path data success error] :as m}]
   (console :debug (str "::firebase-set " m))
   (set-value! path
               data
               (if success success default-set-success-callback)
               (if error error default-set-error-callback))))


;; Effect for updating multiple values in firebase. Optional :success and :error keys for handlers
;; path is the starting db ref node
;; path-data-map is a map of path (list of) and values
(re-frame/reg-fx
 ::firebase-update
 (fn [{:keys [path path-data-map success error]}]
   (update! path
            path-data-map
            (if success success default-set-success-callback)
            (if error error default-set-error-callback))))


;; the key will be stored in the db at key-path
(re-frame/reg-fx
 ::firebase-push
 (fn [{:keys [path data success error key-path] :as m}]
   (console :debug (str "::firebase-push " m))
   (let [key (push-value! path
                          data
                          #(re-frame/dispatch [success])
                          (if error
                            #(re-frame/dispatch [error %])
                            error-callback))]
     (re-frame/dispatch [::write-to-db key-path key]))))

(re-frame/reg-event-db
 ::write-to-db
 (fn [db [_ path data]]
  ;;  (println "path" path)
   (assoc-in db path data)))


(re-frame/reg-fx
 ::firebase-create-user
 (fn [{:keys [email password success error]}]
   (create-user email password
                #(re-frame/dispatch [success %])
                (if error
                  #(re-frame/dispatch [error %])
                  error-callback))))

(re-frame/reg-fx
 ::firebase-sign-in
 (fn [{:keys [email password success error]}]
   (sign-in email password
            #(re-frame/dispatch [success %])
            (if error
              #(re-frame/dispatch [error %])
              error-callback))))

(re-frame/reg-fx
 ::firebase-sign-out
 (fn [{:keys [success error]}]
   (sign-out #(re-frame/dispatch [success %])
             (if error
               #(re-frame/dispatch [error %])
               error-callback))))

;;
;; Settings for FB and reframe
;;
(def temp-path-atom (atom [:temp]))

(defn set-temp-path!
  [new-path]
  (reset! temp-path-atom new-path))

(defn fb-reframe-config
  "Configures the path for the temp storage and initializes firebase app with the provided key. Map keys:
   \n:temp-path vector of strings
   \n:firebase-config map"
  [config]
  {:pre [(is (spec/valid? (spec/keys :req-un [::temp-path ::firebase-config]) config))]}
  (set-temp-path! (:temp-path config))
  (when-not (nil? (:firebase-config config)) (init-app (:firebase-config config)))
  ;; (get-auth)
  )


;; effect for reading once
(defn on-value-once-handler
  [{:keys [path success] :as config}]
  {:pre [(is (spec/valid? (spec/keys :req-un [::path ::success]) config))]}
  (on-value path #(re-frame/dispatch [success %]) true))

(re-frame/reg-fx
 ::on-value-once
 on-value-once-handler)

(re-frame/reg-sub-raw
 ::on-value
 (fn [app-db [_ path]]
   (let [query-token (on-value path
                               #(re-frame/dispatch [::fb-write-to-temp path (if-vector?->map %)]))]
     (ratom/make-reaction
      (fn [] (get-in @app-db (concat @temp-path-atom path)))
      :on-dispose #(do (off path query-token)
                       (re-frame/dispatch [::cleanup-temp path]))))))

;; temp storage for fire-base reads
(re-frame/reg-event-db
 ::fb-write-to-temp
 (fn [db [_ path data]]
   (assoc-in db (concat @temp-path-atom path) data)))


;; clean temp storage
(re-frame/reg-event-db
 ::cleanup-temp
 (fn [db [_ path]]
   (dissoc-in db (concat @temp-path-atom path))))


(re-frame/reg-sub-raw
 ::on-auth-state-changed
 (fn [app-db [_]]
   (let [_ (on-auth-state-changed
            #(re-frame/dispatch [::fb-write-to-temp [:uid] (if %
                                                             (js->clj (.-uid %))
                                                             nil)]))]
     (ratom/make-reaction
      (fn [] (get-in @app-db (concat @temp-path-atom [:uid])))))))


(def get-current-user firebase-auth/get-current-user)
(def get-current-user-uid firebase-auth/get-current-user-uid)
(def get-current-user-email firebase-auth/get-current-user-email)

(defn set-browser-session-persistence
  []
  (firebase-auth/set-browser-session-persistence))

(comment

  (re-frame/reg-event-fx
   ::update-test
   (fn []
     {::firebase-update {:path ["users" (get-current-user-uid)]
                         :path-data-map {["available" "4"] true
                                         ["group-with" "1"] "666"}}}))
  ;; ... and then dispatch it!
  (re-frame/dispatch [::update-test])
  (println (clj->js {:id "1"
                     :age 50
                     :map {:a "3" :b 4}}))


;
  )
