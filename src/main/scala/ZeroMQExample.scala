import akka.actor._
import akka.zeromq._
import java.lang.Thread

case class Begin 

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
}

object ZeroMQActor { 
  def create(system: ActorSystem, name: String) = { 
    println { "Creating Sockets" } 
	val server = system.actorOf(Props[ZeroMQServer],name+"Server")
    val client = system.actorOf(Props[ZeroMQClient],name+"Client")
    println { "Socket Created" } ; 
    (server , client)
  }

  def main( args: Array[String] ) = { 
    println { "Starting Akka ZeroMQ test" }
    val system = ActorSystem("system") 
    val troupe = ZeroMQActor.create(system,"example") 
    val request = system.newReqSocket(Array(SocketType.Req,Listener(troupe._2),Connect("tcp://127.0.0.1:5555")))
    Thread.sleep(1000)
    println { "Sending Request" }
    request ! ZMQMessage(Seq(Frame("Hello World !")))
    println { "Waiting" } 
    Thread.sleep(2000)
    println { "terminating execution" } 
    system.shutdown()
  }
}





