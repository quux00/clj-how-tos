(ns thornydev.sockets.main
  (:require [thornydev.sockets.echo-client :refer [echo-client]]
            [thornydev.sockets.echo-server :refer [do-server]])
  (:gen-class))

(defn -main [& args]
  (if (seq args)
    (case (first args)
      "server" (do-server)
      "client" (echo-client)
      (println "no speakity: " (first args)))
    (println "server or client directive not provided")))
