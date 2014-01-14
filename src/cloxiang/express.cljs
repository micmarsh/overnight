(ns cloxiang.express
      (:use
        [cljs.core.async :only [<!]]
        [clojure.string :only [lower-case]])
      (:use-macros [cljs.core.async.macros :only [go]]))

(defn- async->express [async-callback]
    (fn [request response]
        (let [result (async-callback request)]
            (cond (string? result)
                (.write response result)
                :else ; it's a channel ho shit
                    (go
                        (.write response (<! result)))))))

(defn- register-route [app route]
    (let [[type path callback] route
          fn-name (-> type name lower-case)]
        ((aget app fn-name)
            path
            (async->express callback))))

(defn initialize [& args]
    (let [express (js/require "express")
          http (js/require "http")
          app (express)]
          ; server (.createServer http app)
          ; sockets (accept-sockets server)]
          (doseq [item args]
            (cond (vector? item)
                (register-route app item)))))
