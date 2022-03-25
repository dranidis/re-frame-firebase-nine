(ns re-frame-firebase-nine.example.subs
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.fb-reframe :as fb-reframe]))

(re-frame/reg-sub
 ::todos
 (fn []
   (re-frame/subscribe [::fb-reframe/on-value ["todos"]]))
 (fn [todos]
   (reduce-kv (fn [m k v] (assoc m k (assoc v :id (name k)))) {} todos)))

(re-frame/reg-sub
 ::new-todo-key
 (fn [db]
   (::current-todo-key db)))

(re-frame/reg-sub
 ::selected
 (fn []
   (re-frame/subscribe [::fb-reframe/on-value ["selected"]]))
 (fn [selected]
   selected))

