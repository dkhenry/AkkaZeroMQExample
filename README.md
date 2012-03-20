ZeroMQ Sockets using Scala and Akka
===================================


This is a small example program I put together to display how to use the ZeroMQ Reply and Request Sockets. I find these to be the most usefull sockets to use for RPC , but there is almsot no documentation on them in the Akka document. I will try my best to annotate the process here 


* Create your base Actor Classes 

  ```scala
class ZeroMQServer()extends Actor {   
  // The server instantiates and Ownes the Reply Socket
  val repSocket = context.system.newSocket(SocketType.Rep, Listener(self), Bind("tcp://*:5555"))

  // A simple echo server 
  def receive: Receive = {    
	case Connecting => println {" We are connecting" }
	case m: ZMQMessage => println { "We got a Message: "+m.firstFrameAsString } ; repSocket ! m 
  }
}

class ZeroMQClient extends Actor { 
  // A simple echo client
  def receive = { 
    case Begin => println { "Sending Message" }
	case m: ZMQMessage => println { "We got a reply: "+m.firstFrameAsString } ; sender ! m
  }
}```


* Instantiate the actors 

  ```scala
	val server = system.actorOf(Props[ZeroMQServer],name+"Server")
    val client = system.actorOf(Props[ZeroMQClient],name+"Client")
```

* Create the request socket bound to the client
 
  ```scala
    val request = system.newReqSocket(Array(SocketType.Req,Listener(troupe._2),Connect("tcp://127.0.0.1:5555")))
```

* Send a message 
 
  ```scala
    request ! ZMQMessage(Seq(Frame("Hello World !")))
```

There are still parts that need to be done better like 

* The client should own the connecting socket 
 
* This shouldn't get this during shut down 
 
  > java.lang.IllegalStateException: Promise already completed: akka.dispatch.DefaultPromise@1e64a937 tried to complete with Right(NoResults)
  >     at akka.dispatch.Promise$class.complete(Future.scala:746)
  >     at akka.dispatch.DefaultPromise.complete(Future.scala:811)
  >     at akka.dispatch.Future$$anon$3.run(Future.scala:193)
  >     at akka.dispatch.TaskInvocation.run(AbstractDispatcher.scala:83)
  >     at akka.jsr166y.ForkJoinTask$AdaptedRunnableAction.exec(ForkJoinTask.java:1381)
  >     at akka.jsr166y.ForkJoinTask.doExec(ForkJoinTask.java:259)
  >     at akka.jsr166y.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:997)
  >     at akka.jsr166y.ForkJoinPool.runWorker(ForkJoinPool.java:1495)
  >     at akka.jsr166y.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:104)

* The Thread.sleep's should go away 

However it wors as is so I am incliend to move onto the next part of my project 

DK 
  