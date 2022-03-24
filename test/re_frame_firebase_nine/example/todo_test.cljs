(ns re-frame-firebase-nine.example.todo-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [day8.re-frame.test :as rf-test]
            [re-frame-firebase-nine.example.subs :as subs]
            [re-frame-firebase-nine.example.events :as events]
            [re-frame.core :as re-frame]
            [re-frame-firebase-nine.example.forms.forms :refer [db-set-value!]]
            [re-frame-firebase-nine.emulator :refer [connect-fb-emulator-empty-db]]))


(defn init-emulator-db []
  (connect-fb-emulator-empty-db)
  (re-frame/dispatch-sync [::events/initialize-db]))

(deftest test-create-todo
  (testing "save-read-todo"
    (rf-test/run-test-async
     (init-emulator-db)
     (let [new-todo-key (re-frame/subscribe [::subs/new-todo-key])
           todo {:todo "Test task"}
           todos (re-frame/subscribe [::subs/todos])]
       (re-frame/dispatch [::events/create-todo todo])
       (rf-test/wait-for
        [::events/create-todo-success]
        (println "After create-todo-success")
        (is (= 1 (count (vals @todos))))
        (is (not (nil? @new-todo-key)))
        (is (= (:todo todo) (:todo (first (vals @todos)))))
        (is (= @new-todo-key (:id (first (vals @todos))))))))))

(deftest test-update-todo
  (testing "update-read-todo"
    (rf-test/run-test-async
     (init-emulator-db)
     (let [todos (re-frame/subscribe [::subs/todos])]
       (re-frame/dispatch [::events/create-todo {:todo "Test task"}])
       (rf-test/wait-for
        [::events/create-todo-success]
        (println "After create-todo-success")
        (let [todo-id (:id (first (vals @todos)))
              todo (get @todos (keyword todo-id))
              todo' (assoc todo :todo "new-string" :completed true)]
          (re-frame/dispatch [::events/update-todo todo'])
          (rf-test/wait-for
           [::events/update-todo-success]
           (println "After update-todo-success")
           (is (= "new-string" (:todo (first (vals @todos)))))
           (is (= true (:completed (first (vals @todos))))))))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment

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
    ))


