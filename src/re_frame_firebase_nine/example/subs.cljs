(ns re-frame-firebase-nine.example.subs
  (:require [day8.re-frame-10x.inlined-deps.re-frame.v1v1v2.re-frame.core :refer [reg-sub-raw]]
            [re-frame-firebase-nine.fb-reframe :as fb-reframe]
            [re-frame.core :as re-frame]
            [reagent.ratom :refer [reaction]]))

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



(reg-sub-raw
 ::modal
 (fn [db _] (reaction (:modal @db))))