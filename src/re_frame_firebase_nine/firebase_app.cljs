(ns re-frame-firebase-nine.firebase-app
  (:require ["firebase/app" :as fb]))

;;
;; Initialize firebase app once and store the app in the atom
;;
(defonce firebase-app (atom nil))

(def init-error-msg
  (str "Firebase not initialised! Call init-app with a configuration map: {:apiKey \"xxxxxxxxxx\",
        :authDomain \"xxxxxx.firebaseapp.com\",
        :databaseURL \"https://xxxxxxxxx.firebasedatabase.app\",
        :projectId \"xxxxxxx\",
        :appId \"xxxxxxx\"}"
       " More info at "
       "https://firebase.google.com/docs/web/learn-more#config-object"))

(defn init-app
  ([]
   (if-not @firebase-app
     (throw (js/Error. init-error-msg))
     @firebase-app))
  ([firebase-config]
   (reset! firebase-app (fb/initializeApp (clj->js firebase-config)))
   @firebase-app))


(comment

  (init-app {:apiKey "AIzaSyCpMjjOO9t-TVrzJ-seEy-4MUnH9PWc-uc",
             :authDomain "test-firebase-refr.firebaseapp.com",
             :databaseURL "https://test-firebase-refr-default-rtdb.europe-west1.firebasedatabase.app",
             :projectId "test-firebase-refr",
             :appId "1:1052651152055:web:4971b846529e25aa0ba332"})
  ;
  )