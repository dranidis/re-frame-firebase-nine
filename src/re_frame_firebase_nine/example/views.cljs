(ns re-frame-firebase-nine.example.views
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.example.subs :as subs]
   [re-frame-firebase-nine.example.events :as events]
   [re-frame-firebase-nine.example.forms.forms :refer [input-element]]
   [re-frame-firebase-nine.example.forms.subs :as form-subs]
   [re-frame-firebase-nine.fb-reframe :refer [get-current-user-email]]))


(defn todo-item [todo]
  (let [id (:id todo)
        path [:form :todo-map (keyword id)]]
    [:div {:key (random-uuid)}
     [input-element {:class ""
                     :type :checkbox
                     :placeholder "Completed"
                     :path (into path [:completed])}]
     [input-element {:class ""
                     :type :text
                     :placeholder "I have to do ..."
                     :path (into path [:todo])}]
     [:button {:on-click #(re-frame/dispatch [::events/save-todo path])
               :disabled (not @(re-frame/subscribe [::form-subs/changed-value path]))} "Save"]]))

(defn create-item
  []
  (let [path [:form :todo]]
    [:div
     [:h1 "New todo item"]
     [input-element {:class ""
                     :type :text
                     :placeholder "I have to do ..."
                     :path path}]
     [:button {:on-click #(re-frame/dispatch [::events/create-todo path])} "Create"]]))

(defn main-panel []
  [:div
   [:h1 "Current user email:" (get-current-user-email)]

   [create-item]

   [:h1 "Todos"]
   [:div
    (doall (map todo-item (vals @(re-frame/subscribe [::subs/form-todo-map]))))]])

