# clj-sockets

The code here serves to show how to do a simple socket echo server and client in Clojure using Java sockets.

I use two libraries:

1. A tiny wrapper library I wrote: thornydev.clj-sockets
2. The 1.0.0 [server-socket library](https://github.com/technomancy/server-socket) in [Clojars](https://clojars.org/server-socket)

## Usage

### Version 1

The code for my tiny socket library is in `src/thoryndev/clj_sockets.clj` and its use is shown in the `thornydev.sockets.echo_threaded.clj` file.

You can run it in the REPL like so.  You type in messages when prompted and they come back from the server having been upper cased.

    user=> (require '[thornydev.sockets.echo-threaded :as est])
    nil
    user=> (est/engage)
    Server started.
    Msg for the server:   #_=> hello server
    From echo server: HELLO SERVER
    Msg for the server:   #_=> hello sailor
    From echo server: HELLO SAILOR
    Msg for the server:   #_=> :quit
    Echo server closing down
    nil
    Client closed down
    nil


The engage method spawns a separate thread for the echo-server and runs the client in the main thread.

You could also run these in separate REPLs:

REPL #1:

    user=> (require '[thornydev.sockets.echo-threaded :as est])
    nil
    user=> (est/echo-server)
    Server started.

REPL #2

    user=> (require '[thornydev.sockets.echo-threaded :as est])
    nil
    user=> (est/echo-client)
    Msg for the server:   #_=> From client to server
    From echo server: FROM CLIENT TO SERVER
    Msg for the server:   #_=> :quit
    # etc.



### Version 2 using the server-socket library

There is currently no documentation for this library (and what little you can find on the is for the old contrib version and didn't work for me).

I demonstrate how to use two of its four public methods: `create-server` and `close-server`.

`create-server` spawns a new thread and calls the handler function you provide it.  The `close-server` function closes all four resources that get created:
1. The input stream (which is passed to your handler fn)
2. The output stream (also passed to your handler fn)
3. The Socket object (created via the .accept method)
4. The ServerSocket

When you call `create-server`, you get back a struct that wraps the SocketServer object and a Ref of its connections:

    user=> (require 'thornydev.sockets.echo-server)
    nil
    user=> (ns thornydev.sockets.echo-server)
    nil
    thornydev.sockets.echo-server=> (require '[server.socket :refer [create-server close-server]])
    nil
    thornydev.sockets.echo-server=> (def sock (create-server 8000 echo-server))
    #'thornydev.sockets.echo-server/sock
    thornydev.sockets.echo-server=> sock
    {:server-socket #<ServerSocket ServerSocket[addr=0.0.0.0/0.0.0.0,port=0,localport=8000]>,
     :connections #<Ref@4cfdbb9f: #{}>}
    thornydev.sockets.echo-server=> @(:connections sock)
    #{}
    
You pass this struct to the `close-server` when you are done.  However, since `create-server` puts the socket server its own thread, I've had to use a CountDownLatch to wait upon in the main thread to detect when the server has closed down in order to call `close-server` and release those 4 resources.


#### Three ways to run it:

**Option 1:** Use two REPLs as described above
* REPL 1 invokes (thornydev.sockets.main/-main "server")
* REPL 2 invokes (thornydev.sockets.main/-main "client")

**Option 2:** Build an uberjar and run it from java, again one JVM running the server and one running the client

    $ lein uberjar
    # in console 1
    $ java -jar target/clj-sockets-0.1.0-standalone.jar server
    # in console 2
    $ java -jar target/clj-sockets-0.1.0-standalone.jar client
    Msg for the server: message 123
    From echo server: MESSAGE 123
    Msg for the server: I am from the future!
    From echo server: I AM FROM THE FUTURE!
    Msg for the server: :quit
    nil
    Client closed down

**Option 3:** Run with lein trampoline

   # in console 1
   $ lein trampoline run server

   # in console 2
   $ lein trampoline run client
   # ... same routine as above ...


## License

Copyright © 2012 Michael Peterson
Distributed under the Eclipse Public License, the same as Clojure.
