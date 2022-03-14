(ns re-frame-firebase-nine.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [re-frame-firebase-nine.utils :refer [if-vector?->map vector->map]]))

(deftest vector->map-test
  (testing "empty list to empty map"
    (is (= {} (vector->map []))))
  (testing "one element list to a map with :0 keyword"
    (is (= {:0 1} (vector->map [1]))))
  (testing "a list to a map with nil values"
    (is (= {:0 "a" :1 3 :2 true} (vector->map ["a" 3 true nil]))))
  (testing "a list with nil values"
    (is (= {:1 true  :3 true} (vector->map [nil true nil true])))))

(deftest if-vector?->map-test
  (testing "not a list leave as it is"
    (is (= {} (if-vector?->map {}))))
  ;; (testing "not a list leave as it is"
  ;;   (is (= {:a 1 :b [2 1]} (if-vector?->map {:a 1 :b [2 1]}))))
  (testing "empty list to empty map"
    (is (= {} (if-vector?->map []))))
  (testing "one element list to a map with :0 keyword"
    (is (= {:0 1} (if-vector?->map [1]))))
  (testing "a list to a map"
    (is (= {:0 "a" :1 3 :2 true} (if-vector?->map ["a" 3 true nil])))))

(deftest list-inside-map
  (testing "first"
    (is (= {:a {:0 1}} (if-vector?->map {:a [1]}))))
  (testing "second"
    (is (= {:a 1 :b {:0 1}} (if-vector?->map {:a 1 :b [1]}))))
  (testing "third"
    (is (= {:a 1 :b {:0 1} :c {:0 1 :1 2}} (if-vector?->map {:a 1 :b [1] :c [1 2]}))))
  (testing "second level"
    (is (= {:a 1 :b {:b1 {:0 4 :1 5}} :c {:0 1 :1 2}} (if-vector?->map {:a 1 :b {:b1 [4 5]} :c [1 2]}))))
  (testing "third level"
    (is (= {:a 1 :b {:b1 {:0 4 :1 5} :b2 {:b3 {:0 56 :1 67}}} :c {:0 1 :1 2}}
           (if-vector?->map {:a 1 :b {:b1 [4 5] :b2 {:b3 [56 67]}} :c [1 2]}))))
  ;
  )