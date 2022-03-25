(ns re-frame-firebase-nine.example.subs
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.fb-reframe :as fb-reframe]
   [re-frame-firebase-nine.example.forms.forms :refer [db-set-value!]]))

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
 (fn [selected [_ form-path]]
   ;; changing of the form-path should happer here so that it happens only once
   ;; and not when component rerenders due to drop-down changing the dom
   ;; and causing rerender which resets the value to the one read from 
   ;; the subscription.
   (db-set-value! form-path selected)
   selected))