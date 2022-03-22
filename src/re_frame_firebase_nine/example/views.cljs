(ns re-frame-firebase-nine.example.views
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.example.subs :as subs]
   [re-frame-firebase-nine.example.events :as events]
   [re-frame-firebase-nine.example.forms.forms :refer [input-element]]
   [re-frame-firebase-nine.example.forms.subs :as form-subs]
   [re-frame-firebase-nine.fb-reframe :refer [get-current-user-email]]))


(defn todo-item [todo]
  (let [id (:id todo)]
    [:div {:key (random-uuid)}
     [input-element {:class ""
                     :type :checkbox
                     :placeholder "Completed"
                     :path [:form :todo-map (keyword id) :completed]}]
     [input-element {:class ""
                     :type :text
                     :placeholder "I have to do ..."
                     :path [:form :todo-map (keyword id) :todo]}]
     [:button {:on-click #(re-frame/dispatch [::events/save-todo [:form :todo-map (keyword id)]])
               :disabled (not @(re-frame/subscribe [::form-subs/changed-value [:form :todo-map (keyword id)]]))} "Save"]]))

(defn main-panel []
  [:div
   [:h1 "Current user email:" (get-current-user-email)]

   [:h1 "New todo item"]
   [input-element {:class ""
                   :type :text
                   :placeholder "I have to do ..."
                   :path [:form :todo]}]
   [:button {:on-click #(re-frame/dispatch [::events/create-todo [:form :todo]])} "Create"]
   [:h1 "Todos"]
   [:div
    (doall (map todo-item @(re-frame/subscribe [::subs/form-todo-map])))]])
