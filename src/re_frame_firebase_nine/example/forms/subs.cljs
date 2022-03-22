(ns re-frame-firebase-nine.example.forms.subs
  (:require [re-frame.core :as re-frame]
            [re-frame-firebase-nine.example.forms.utils :refer [is-substring?]]))


(re-frame/reg-sub
 ::get-value
 (fn [db [_ path]]
   (get-in db path)))

(re-frame/reg-sub
 ::changed-value
 (fn [db [_ path]]
   ;; Logical DISJUNCTION of all the changed-values (which are either true or false)
   (reduce-kv (fn [init _ v] (or init v)) false
              (get-in db (into [:form-changed-value] path)))))

(re-frame/reg-sub
 ::dropdown-select-options
 (fn [db [_ path all-options {:keys [sort? by]}]]
   (let [unsorted (->> all-options
                       (filter
                        (fn [{:keys [_ name]}]
                          (or (nil? (get-in db path)) (is-substring? (get-in db path) name)))))]
     (if sort?
       (sort (fn [g1 g2] (< (get g1 by) (get g2 by))) unsorted)
       unsorted))))

(re-frame/reg-sub
 ::dropdown-select-size
 (fn [[_ path all-options]]
   (re-frame/subscribe [::dropdown-select-options path all-options]))
 (fn [options]
   ;; an extra option is (Nothing)
   (min 10 (inc (count options)))))
