(ns re-frame-firebase-nine.example.emulator
  (:require ["firebase/database" :as fdb]
            ["firebase/auth" :as fauth]
            [re-frame-firebase-nine.firebase-database :refer [set-value! get-db]]
            [re-frame-firebase-nine.firebase-auth :refer [get-auth]]))

(defn get-config
  []
  {:apiKey "AIzaSyBBbED8EWbMEDYxkeinMRqNXyb9Vr18C9A",
   :authDomain "re-frame-firebase-nine-example.firebaseapp.com",
   :databaseURL "https://re-frame-firebase-nine-example-default-rtdb.europe-west1.firebasedatabase.app",
   :projectId "re-frame-firebase-nine-example",
   :storageBucket "re-frame-firebase-nine-example.appspot.com",
   :messagingSenderId "1052132575682",
   :appId "1:1052132575682:web:a0d28b5405ab1825e8ee5b"})


(comment

  (.-hostname js/location)

  (set-value! ["users"] "test-value"
              #(println "SUCCESS") #(println "ERROR" (js->clj %)))
 ;
  )