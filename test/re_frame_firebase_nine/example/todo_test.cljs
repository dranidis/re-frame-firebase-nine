(ns re-frame-firebase-nine.example.todo-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [day8.re-frame.test :as rf-test]
            [re-frame-firebase-nine.fb-reframe :refer [fb-reframe-config connect-emulator] :as fb]
            [re-frame-firebase-nine.example.fb-config :refer [get-config]]
            [re-frame-firebase-nine.example.subs :as subs]
            [re-frame-firebase-nine.example.events :as events]
            [re-frame.core :as re-frame]
            [re-frame-firebase-nine.example.forms.forms :refer [db-set-value!]]
            [re-frame-firebase-nine.emulator :refer [connect-fb-emulator-empty-db]]))



(deftest test-create-todo
  (testing "save-read-todo"
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
       (rf-test/wait-for [::events/create-todo-success]
                         (is (= 1 (count (vals @tasks))))
                         (is (= task (:todo (first (vals @tasks))))))))))



