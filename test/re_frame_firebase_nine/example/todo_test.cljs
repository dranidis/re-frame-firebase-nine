(ns re-frame-firebase-nine.example.todo-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [day8.re-frame.test :as rf-test]
            [re-frame-firebase-nine.example.subs :as subs]
            [re-frame-firebase-nine.example.events :as events]
            [re-frame.core :as re-frame]
            [re-frame-firebase-nine.example.forms.forms :refer [db-set-value!]]
            [re-frame-firebase-nine.emulator :refer [connect-fb-emulator-empty-db]]))

(re-frame/reg-event-fx
 ::event
 (fn [_]
   (println "EVENT")
   {:dispatch [::next-event]}))

(re-frame/reg-event-fx
 ::next-event
 (fn [_]
   (println "NEXT EVENT")
   {}))

;; fails with sync ; does not fail with async
(deftest test-todo-1
  (testing "save-read-todo-2"
    (rf-test/run-test-sync
     (is (= 0 0))
     ;
     )
    ;
    ))

;; (deftest test-todo-2
;;   (testing "save-read-todo-2"
;;     ;; does not fail and does not execute all tests
;;     ;; (rf-test/run-test-async
;;     ;;  (is (= 5 6))
;;     ;;  ;
;;     ;;  )
;;     ;
;;     ))

(deftest test-todo-3
  (testing "save-read-todo-2"
    (rf-test/run-test-async
     (re-frame/dispatch [::event])
     (rf-test/wait-for [::next-event]
                       (println "TESTING 5-6")
                       (is (= 6 6))
                       (is (= 7 7))
     ;
                       )))
    ;
  )




(deftest test-todo
  (testing "save-read-todo"
    (rf-test/run-test-async
     (let [db (re-frame/subscribe [::subs/db])
           _ (connect-fb-emulator-empty-db)
           _ (re-frame/dispatch-sync [::events/initialize-db])
           old-key (:current-todo-key @db)
           task "Test task"
           _ (db-set-value! [:form :todo] task)
           _ (re-frame/dispatch [::events/create-todo [:form :todo]])
           tasks (re-frame/subscribe [::subs/todos])
          ;;  _ (re-frame/dispatch [::events/dispatch-later])
           ]

       (rf-test/wait-for [::events/create-todo-success]
                         (println "After create-todo-success")
                         (let []
                           (is (= 1 old-key))
                           (is (= 1 (count (vals @tasks))))
                           (is (not (nil? (:current-todo-key @db))))
                           (is (= nil (:todo (first (vals @tasks))))))))))

  (testing "create-upate-read-todo"
    (rf-test/run-test-async
     (let [_ (re-frame/subscribe [::subs/db])
           _ (connect-fb-emulator-empty-db)
           _ (re-frame/dispatch-sync [::events/initialize-db])
           task "Test task"
           _ (db-set-value! [:form :todo] task)
           _ (re-frame/dispatch [::events/create-todo [:form :todo]])
           tasks (re-frame/subscribe [::subs/todos])
          ;;  _ (re-frame/dispatch [::events/dispatch-later])
           ]
       (rf-test/wait-for
        [::events/create-todo-success]
        (let []))))))




