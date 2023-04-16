(ns re-frame-firebase-nine.firebase-database
  (:require ["firebase/database" :as fdb]
            [clojure.string :as string]
            [re-frame.loggers :refer [console]]))

;; (defn- get-db
;;   []
;;   (fdb/getDatabase (init-app)))

(defn get-db
  []
  (fdb/getDatabase))

(def connect-database-emulator fdb/connectDatabaseEmulator)

(defn fb-ref
  ([db] (fdb/ref db))
  ([db path] (fdb/ref db (string/join "/" path))))

(defn fb-set!
  ([ref data] (fb-set! ref data (fn []) (fn [_])))
  ([ref data then-callback catch-callback]
   (-> (fdb/set
        ref (clj->js data))
       (.then then-callback)
       (.catch catch-callback))))

(defn fb-push
  [path]
  (fdb/push (fb-ref (get-db) path)))
;;
;; public functions
;;
(defn set-value!
  ([path data] (set-value! path data (fn []) (fn [_])))
  ([path data then-callback catch-callback]
   (fb-set! (fb-ref (get-db) path) data then-callback catch-callback)))


;; Use the push () method to append data to a list in multiuser applications. 
;; The push () method generates a unique key every time a new child is added to the specified Firebase reference.
;; The .key property of a push () reference contains the auto-generated key.
;; (defn- push-ref
;;   [path]
;;   (fdb/push (fb-ref (get-db) path)))

(defn push-value!
  ([path data] (push-value! path data (fn []) (fn [_])))
  ([path data then-callback catch-callback]
   (let [ref (fb-push path)]
     (fb-set! ref data then-callback catch-callback)
     (.-key ref))))

;; Writes multiple values to the Database at once.
;; The values argument contains multiple property-value pairs that will be written to the Database together.
(defn update!
  ([path set-map] (update! path set-map (fn []) (fn [_])))
  ([path set-map then-callback catch-callback]
   (-> (fdb/update (fb-ref (get-db) path)
                   (clj->js (->> set-map
                                 (map (fn [[k v]] [(str "/" (string/join "/" k)) v]))
                                 (into {}))))
       (.then then-callback)
       (.catch catch-callback))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn on-value
  "Default only-once is false"
  ([path callback] (on-value path callback false))
  ([path callback only-once?]
   (fdb/onValue (fb-ref (get-db) path)
                (fn [snap-shot]
               ;; ^js https://shadow-cljs.github.io/docs/UsersGuide.html#infer-externs
                  (callback (js->clj (.val ^js snap-shot) :keywordize-keys true)))
                #js {:onlyOnce only-once?})))

(defn off
  "Detach listeners"
  ([path] (fdb/off (fb-ref (get-db) path)))
  ([path listener]
   (fdb/off (fb-ref (get-db) path) listener)))



(defn default-set-success-callback
  [])

(defn default-set-error-callback
  [error]
  (console :error (js->clj error)))


(comment

  ;;
;; Initialize firebase app once and store the app in the atom
;;
  (defonce firebase-connected (atom nil))
  (defn set-connectecd [v]
    (console :debug (str "Connected: " v))
    (reset! firebase-connected v))
  (on-value [".info/connected"] set-connectecd)
  @firebase-connected)

