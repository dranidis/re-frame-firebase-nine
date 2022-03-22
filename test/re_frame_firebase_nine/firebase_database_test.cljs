(ns re-frame-firebase-nine.firebase-database-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [re-frame-firebase-nine.firebase-database :refer [set-value! on-value push-value! update!]]
            [re-frame-firebase-nine.emulator :refer [connect-fb-emulator-empty-db]]))

(deftest test-fb
  (testing "set-value!-on-value-once"
    (let [_ (connect-fb-emulator-empty-db)
          value "value"
          _ (set-value! ["test"] "value")
          from-db (atom nil)
          _ (on-value ["test"] #(reset! from-db %)  true)]
      (is (= value @from-db))))

  (testing "push-value!-on-value-once"
    (let [_ (connect-fb-emulator-empty-db)
          value "value"
          ref (push-value! ["test"] "value")
          from-db (atom nil)
          _ (on-value ["test" ref] #(reset! from-db %)  true)]
      (is (= value @from-db))))

  (testing "update!-on-value-once"
    (let [_ (connect-fb-emulator-empty-db)
          value "value"
          value2 "value2"
          _ (update! ["test"] {["test1"] value
                               ["test2" "test21"] value2})
          from-db (atom nil)
          from-db2 (atom nil)
          _ (on-value ["test" "test1"] #(reset! from-db %)  true)
          _ (on-value ["test" "test2" "test21"] #(reset! from-db2 %)  true)]
      (is (= value @from-db))
      (is (= value2 @from-db2))))

  (testing "on-value"
    (let [_ (connect-fb-emulator-empty-db)
          from-db (atom nil)
          _ (on-value ["test"] #(reset! from-db %))]
      (is (= nil @from-db))
      (set-value! ["test"] "value")
      (is (= "value" @from-db))
      (set-value! ["test"] "updated")
      (is (= "updated" @from-db)))))


