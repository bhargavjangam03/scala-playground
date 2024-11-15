package services

import models.{Visitor, VisitorIdentityProof}
import repositories.{VisitorIdentityProofRepository, VisitorRepository}

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class VisitorService @Inject()(VisitorRepository: VisitorRepository,
                               visitorIdentityProofRepository: VisitorIdentityProofRepository,
                               KafkaProducerService: KafkaProducerService
) {

  private var visitors: List[Visitor] = List()

  def checkIn(visitorData: Visitor): Future[Long] = {
    val persistedVisitorFuture: Future[Long] = VisitorRepository.create(visitorData)
    // Then, asynchronously send the visitor data to Kafka after persistence
    persistedVisitorFuture.map { visitorId =>
      // Send the visitor data to Kafka
      KafkaProducerService.sendToKafka(visitorData)

      // Return the ID from the persistence operation (you can use it as needed)
      visitorId
    }
  }

  def addVisitorIdentity(visitorIdentityData: VisitorIdentityProof): Future[Long] = {
    visitorIdentityProofRepository.create(visitorIdentityData)
  }

  def checkOut(visitorId: Long): Future[Boolean] = {
    VisitorRepository.updateCheckOut(visitorId).flatMap {
      case Some(visitor) =>
        // Push the updated visitor to Kafka
        KafkaProducerService.sendToKafka(visitor)
        Future.successful(true)
      case None =>
        Future.successful(false)
    }
  }

  def approve(visitorId: Long): Future[Boolean] = {
    VisitorRepository.updateVisitorStatus(visitorId, "Approved").flatMap {
      case Some(visitor) =>
        // Push the updated visitor to Kafka
        KafkaProducerService.sendToKafka(visitor)
        Future.successful(true)
      case None =>
        Future.successful(false)
    }
  }

  def reject(visitorId: Long): Future[Boolean] = {
    VisitorRepository.updateVisitorStatus(visitorId, "Rejected").flatMap {
      case Some(visitor) =>
        // Push the updated visitor to Kafka
        KafkaProducerService.sendToKafka(visitor)
        Future.successful(true)
      case None =>
        Future.successful(false)
    }
  }

  def list(): Future[Seq[Visitor]] = VisitorRepository.list()

  def get(id: Long): Future[Option[Visitor]] = VisitorRepository.getById(id)


//  def update(id: Long, Visitor: Visitor): Future[Option[Visitor]] =
//    VisitorRepository.update(id, Visitor)
//
//  def delete(id: Long): Future[Boolean] = VisitorRepository.delete(id)
}
