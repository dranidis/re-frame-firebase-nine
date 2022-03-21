(ns re-frame-firebase-nine.example.views
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.example.subs :as subs]
   [re-frame-firebase-nine.example.events :as events]
   [re-frame-firebase-nine.example.forms.forms :refer [input-element]]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello from " @name]
     [:h1 "New todo item"]
     [input-element {:class ""
                     :type :text
                     :placeholder "I have to do ..."
                     :path [:form :todo]}]
     [:button {:on-click #(re-frame/dispatch [::events/create-todo [:form :todo]])} "Create"]
     [:h1 "Todos"]
     [:ul
      (map (fn [todo]
             [:li {:key (:id todo)} (:todo todo)])
           (vals @(re-frame/subscribe [::subs/todos])))]]))
