(ns re-frame-firebase-nine.example.views
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.example.subs :as subs]
   [re-frame-firebase-nine.example.events :as events]
   [re-frame-firebase-nine.example.forms.forms :refer [input-element db-get-ref db-set-value! dropdown-search]]
   [re-frame-firebase-nine.fb-reframe :refer [get-current-user-email]]
   [re-frame-firebase-nine.example.forms.utils :refer [if-nil?->value]]))


(defn update-item [todo]
  (let [id (:id todo)
        form-path [:form :edit-todo id]]
    (db-set-value! form-path todo)
    [:div {:key (random-uuid)}
     [input-element {:class ""
                     :type :checkbox
                     :placeholder "Completed"
                     :path (into form-path [:completed])}]
     [input-element {:class ""
                     :type :text
                     :placeholder "I have to do ..."
                     :path (into form-path [:todo])}]
     [:button {:on-click #(re-frame/dispatch [::events/update-todo @(db-get-ref form-path)])
              ;;  :disabled (not @(re-frame/subscribe [::form-subs/changed-value form-path]))
               }"Save"]]))

(defn create-item
  []
  (let [form-path [:form :create-todo]]
    (db-set-value! form-path {})
    [:div
     [:h1 "New todo item"]
     [input-element {:class ""
                     :type :text
                     :placeholder "I have to do ..."
                     :path (into form-path [:todo])}]
     [:button {:on-click (fn [_]
                           (re-frame/dispatch [::events/create-todo @(db-get-ref form-path)])
                           (db-set-value! form-path {}))} "Create"]]))

(defn select-task
  [options]
  (let [form-path [:form :select-task]
    ;; subscribe to the stored selected value and pass the form-path to set the
    ;; initial value
        selected @(re-frame/subscribe [::subs/selected form-path])]
    (println "SELECTED" selected)
    [:div
     [:h1 "Select a task"]
     [:div "Selected: " selected]
     (dropdown-search {:db-path (into form-path)
                       :options (if-nil?->value options [])
                       :id-keyword :id
                       :display-keyword :todo
                       :button-text-empty "Click to select a game"
                       :input-placeholder "Type to find a game"
                       :select-nothing-text "(no selection)"
                       :sort? true
                       :style {:width "300px"}})
     [:button {:on-click (fn [_]
                           (re-frame/dispatch [::events/save-selected @(db-get-ref form-path)]))} "Save"]]))
(defn main-panel []
  [:div
   [:h1 "Current user email:" (get-current-user-email)]
   [create-item]
   [:h1 "Todos"]
   [:div
    (doall (map update-item (vals @(re-frame/subscribe [::subs/todos]))))]
   [select-task
    (vals @(re-frame/subscribe [::subs/todos]))]])

