(ns re-frame-firebase-nine.example.events
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.example.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [re-frame-firebase-nine.fb-reframe :as fb-reframe]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-fx
 ::create-todo
 (fn-traced
  [{:keys [db]} [_ path]]
  {:db (assoc-in db path "")
   ::fb-reframe/firebase-push {:path ["todos"]
                               :data {:todo (get-in db path)}
                               :success #(println "Success")
                               :key-path [:current-todo-key]}}))