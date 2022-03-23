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

(defn create-a-task
  [task-string]
  (db-set-value! [:form :todo] task-string)
  (re-frame/dispatch [::events/create-todo [:form :todo]]))

(deftest test-todo
  (testing "save-read-todo"
    (rf-test/run-test-async
     (let [new-todo-key (re-frame/subscribe [::subs/new-todo-key])
           _ (init-emulator-db)
           task "Test task"
           _ (create-a-task task)
           tasks (re-frame/subscribe [::subs/todos])]
       (rf-test/wait-for
        [::events/create-todo-success]
        (println "After create-todo-success")
        (is (= 1 (count (vals @tasks))))
        (is (not (nil? @new-todo-key)))
        (is (= task (:todo (first (vals @tasks)))))
        (is (= @new-todo-key (:id (first (vals @tasks))))))))))

(deftest test-todo-update
  (testing "update-read-todo"
    (rf-test/run-test-async
     (let [_ (init-emulator-db)
           tasks (re-frame/subscribe [::subs/todos])
           form-todo-map (re-frame/subscribe [::subs/form-todo-map])]
       (create-a-task "A task")
       (rf-test/wait-for
        [::events/create-todo-success]
        (println "After create-todo-success")
        (let [task-id (:id (first (vals @tasks)))
              task-to-update (get @tasks (keyword task-id))
              _ (println "TASKS :" @tasks)
              _ (println "TASK :" task-to-update)
              path [:form :todomap (keyword task-id)]]
          (db-set-value! (into path [:todo]) "new-string")
          (db-set-value! (into path [:completed]) true)
          (db-set-value! (into path [:id]) task-id)
          (re-frame/dispatch [::events/save-todo path])

          (rf-test/wait-for [::events/save-todo-success]
                            (println "After save-todo-success")
                            (is (= "new-string" (:todo (first (vals @form-todo-map)))))
                            (is (= true (:completed (first (vals @form-todo-map))))))))))))





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
  )


)


