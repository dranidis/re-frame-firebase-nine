(ns re-frame-firebase-nine.example.fb-config 
  (:require [re-frame-firebase-nine.example.env-variables :refer [api-key-env]]))

(defn get-config
  []
  {:apiKey api-key-env,
   :authDomain "re-frame-firebase-nine-example.firebaseapp.com",
   :databaseURL "https://re-frame-firebase-nine-example-default-rtdb.europe-west1.firebasedatabase.app",
   :projectId "re-frame-firebase-nine-example",
   :storageBucket "re-frame-firebase-nine-example.appspot.com",
   :messagingSenderId "1052132575682",
   :appId "1:1052132575682:web:a0d28b5405ab1825e8ee5b"})
