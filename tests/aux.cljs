(ns aux
  (:require ["@testing-library/react" :as tlr]
            [shadow.cljs.modern :refer [defclass]]))

(defn mock-window-fns []
  (let [mock-fn (fn [& _args] nil)
        get-computed-style (.-getComputedStyle js/window)
        resize-observer (defclass ResizeObserver
                          (constructor [this] nil)
                          Object
                          (observe [this & args] nil)
                          (unobserve [this & args] nil)
                          (disconnect [this & args] nil))]

    (set! (.-getComputedStyle js/window)
          (fn [elt]
            (get-computed-style elt)))

    (.defineProperty js/Object js/window
                     "matchMedia"
                     #js {:writable true,
                          :value (fn [query]
                                   #js {:matches false,
                                        :media query,
                                        :onchange nil,
                                        :addListener mock-fn,
                                        :removeListener mock-fn,
                                        :addEventListener mock-fn,
                                        :removeEventListener mock-fn,
                                        :dispatchEvent mock-fn})})

    (set! (.-ResizeObserver js/window)
          resize-observer)))

(defn async-setup []
  (mock-window-fns))

(defn async-cleanup []
  (tlr/cleanup))

(defn sync-setup [f]
  (f)
  (tlr/cleanup))
