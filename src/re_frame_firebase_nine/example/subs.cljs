(ns re-frame-firebase-nine.example.subs
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.fb-reframe :as fb-reframe]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::todos
 (fn []
   (re-frame/subscribe [::fb-reframe/on-value ["todos"]]))
 (fn [todos]
   (println "todos" todos)
   (reduce-kv (fn [m k v] (assoc m k (assoc v :id (name k)))) {} todos)))