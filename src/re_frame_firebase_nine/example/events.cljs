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
                               :success ::create-todo-success
                               :key-path [:current-todo-key]}}))

(re-frame/reg-event-fx
 ::save-todo
 (fn-traced
  [{:keys [db]} [_ path]]
  {:db (assoc-in db path "")
   ::fb-reframe/firebase-set {:path ["todos" (:id (get-in db path))]
                              :data  (get-in db path)
                              :success #(println "SAVED")}}))

(re-frame/reg-event-db
 ::create-todo-success
 (fn-traced [db _]
            (let [_ (println "Created task with id:" (get-in db [:current-todo-key]))] db)))

(re-frame/reg-event-fx
 ::dispatch-later
 (fn-traced
  [_ [_ _]]
  {:fx [[:dispatch-later {:ms 200 :dispatch [::later]}]]}))

(re-frame/reg-event-db
 ::later
 (fn-traced [db _]
            (println "LATER")
            db))
