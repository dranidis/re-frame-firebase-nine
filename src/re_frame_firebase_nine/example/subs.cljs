(ns re-frame-firebase-nine.example.subs
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.fb-reframe :as fb-reframe]
   [re-frame-firebase-nine.example.forms.events :as form-events]))

(re-frame/reg-sub
 ::db
 (fn [db]
   db))

(re-frame/reg-sub
 ::todos
 (fn []
   (re-frame/subscribe [::fb-reframe/on-value ["todos"]]))
 (fn [todos]
  ;;  (println "todos" todos)
   (reduce-kv (fn [m k v] (assoc m k (assoc v :id (name k)))) {} todos)))

(re-frame/reg-sub
 ::form-todo-map
 :<- [::todos]
 (fn [todos]
  ;;  (println "SUBS form" todos)
   (re-frame/dispatch [::form-events/set-value! [:form :todo-map] todos])
   (vals todos)))
