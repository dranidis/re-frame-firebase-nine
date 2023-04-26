(ns re-frame-firebase-nine.example.events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame-firebase-nine.example.db :as db]
            [re-frame-firebase-nine.example.subs :as subs]
            [re-frame-firebase-nine.fb-reframe :as fb-reframe]
            [re-frame.core :as re-frame :refer [reg-event-db]]
            [re-frame.loggers :refer [console]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-fx
 ::create-todo
 (fn-traced
  [_ [_ todo]]
  {::fb-reframe/firebase-push {:path ["todos"]
                               :data todo
                               :success #(re-frame/dispatch [::create-todo-success todo])
                               :error #(console :debug "ERROR Creating")
                               :key-path [::subs/current-todo-key]}}))
(re-frame/reg-event-db
 ::create-todo-success
 (fn-traced
  [db [_ todo]]
  (let [_ (println "Created task with id:" @(re-frame/subscribe [::subs/new-todo-key]) 
                   " and data: " todo)]
    db)))

(re-frame/reg-event-fx
 ::update-todo
 (fn-traced
  [_ [_ todo]]
  {::fb-reframe/firebase-set {:path ["todos" (name (:id todo))]
                              :data  todo
                              :success (fn []
                                        ;;  (.alert js/window "Saved")
                                         (re-frame/dispatch [::update-todo-success]))}}))
(re-frame/reg-event-db
 ::update-todo-success
 (fn-traced [db _]
            (let [_ (println "Saved task")]
              db)))

(re-frame/reg-event-fx
 ::delete-todo
 (fn-traced
  [_ [_ todo]]
  {::fb-reframe/firebase-set {:path ["todos" (name (:id todo))]
                              :data  nil
                              :success (fn []
                                        ;;  (.alert js/window "Saved")
                                         (re-frame/dispatch [::delete-todo-success]))}}))
(re-frame/reg-event-db
 ::delete-todo-success
 (fn-traced [db _]
            (let [_ (println "Deleted task")]
              db)))

(re-frame/reg-event-fx
 ::save-selected
 (fn-traced
  [_ [_ todo]]
  {::fb-reframe/firebase-set {:path ["selected"]
                              :data  todo
                              :success (fn []
                                        ;;  (.alert js/window "Saved")
                                         (re-frame/dispatch [::save-selected-success]))}}))

(re-frame/reg-event-db
 ::save-selected-success
 (fn-traced [db _]
            (let [_ (println "Saved selected")]
              db)))



(reg-event-db
 :modal-event
 (fn [db [_ data]]
   (assoc-in db [:modal] data)))