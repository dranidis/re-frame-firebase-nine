(ns re-frame-firebase-nine.example.views
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.example.subs :as subs]
   [re-frame-firebase-nine.example.events :as events]
   [re-frame-firebase-nine.example.forms.forms :refer [input-element db-get-ref db-set-value!]]
   [re-frame-firebase-nine.fb-reframe :refer [get-current-user-email]]))


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

(defn main-panel []
  [:div
   [:h1 "Current user email:" (get-current-user-email)]
   [create-item]
   [:h1 "Todos"]
   [:div
    (doall (map update-item (vals @(re-frame/subscribe [::subs/todos]))))]])

