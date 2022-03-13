(ns re-frame-firebase-nine.fb-reframe
  (:require [re-frame.core :as re-frame]
            [re-frame-firebase-nine.firebase-database :refer [set-value! default-set-success-callback default-set-error-callback on-value off push-value!]]
            [reagent.ratom :as ratom]
            [re-frame.utils :refer [dissoc-in]]
            [re-frame-firebase-nine.firebase-auth :as firebase-auth :refer [error-callback sign-in sign-out create-user]]
            [re-frame-firebase-nine.firebase-app :refer [init-app]]))

;; Effect for setting a value in firebase. Optional :success and :error keys for handlers
;; Data can be deleted by giving null as value
(re-frame/reg-fx
 ::firebase-set
 (fn [{:keys [path data success error]}]
   (set-value! path
               data
               (if success success default-set-success-callback)
               (if error error default-set-error-callback))))

;; the key will be stored in the db at key-path
(re-frame/reg-fx
 ::firebase-push
 (fn [{:keys [path data success error key-path]}]
   (println "key-path" key-path)
   (let [key (push-value! path
                          data
                          (if success success default-set-success-callback)
                          (if error error default-set-error-callback))]
     (re-frame/dispatch [::write-to-db key-path key]))))

(re-frame/reg-event-db
 ::write-to-db
 (fn [db [_ path data]]
   (println "path" path)
   (assoc-in db path data)))


(re-frame/reg-fx
 ::firebase-create-user
 (fn [{:keys [email password success]}]
   (create-user email password
                #(re-frame/dispatch [success %])
                error-callback)))

(re-frame/reg-fx
 ::firebase-sign-in
 (fn [{:keys [email password success]}]
   (sign-in email password
            #(re-frame/dispatch [success %])
            error-callback)))

(re-frame/reg-fx
 ::firebase-sign-out
 (fn [{:keys [success]}]
   (sign-out #(re-frame/dispatch [success %])
             error-callback)))

;;
;; Settings for FB and reframe
;;
(def temp-path-atom (atom [:temp]))

(defn set-temp-path!
  [new-path]
  (reset! temp-path-atom new-path))

(defn fb-reframe-config
  [{:keys [temp-path firebase-config]}]
  (set-temp-path! temp-path)
  (init-app firebase-config))

;;
;; Utility functions for transforming lists to maps
;; Firebase returns a list when the keys are numbers 0, 1, 2
;;
(defn- vector->map
  "Tranforms a vector of elements to a map with the stringified indices as keys.
   e.g. [0 3 4] => {\"0\": 0 \"1\": 3 \"2\": 4}"
  ([list] (vector->map list 0))
  ([list n]
   (if (empty? list) {}
       (assoc (vector->map (rest list) (inc n)) (keyword (str n)) (first list)))))

(defn- if-vector->map
  [value]
  ;; (println "if-vector->map" value (and @turn-lists-to-maps-atom? (vector? value) (vector->map value)))
  (if (vector? value)
    (vector->map value)
    value))

(comment
  (vector->map [true nil true true])

  ;
  )

(re-frame/reg-sub-raw
 ::on-value
 (fn [app-db [_ path]]
   (let [query-token (on-value path
                               #(re-frame/dispatch [::fb-write-to-temp path (if-vector->map %)]))]
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


(def get-current-user firebase-auth/get-current-user)
(def get-current-user-uid firebase-auth/get-current-user-uid)
(def get-current-user-email firebase-auth/get-current-user-email)

(defn set-browser-session-persistence
  []
  (firebase-auth/set-browser-session-persistence))

