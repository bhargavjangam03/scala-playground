package services

import models.VisitorIdentityProof
import models.request.Visitor
import repositories.{VisitorIdentityProofRepository, VisitorRepository}

import javax.inject._
import scala.concurrent.Future

@Singleton
class VisitorService @Inject()(visitorRepository: VisitorRepository,
                               visitorIdentityProofRepository: VisitorIdentityProofRepository,
) {

  private var visitors: List[Visitor] = List()

  def checkIn(visitorData: Visitor): Future[Int] = {
    visitorRepository.create(visitorData)
  }

  def addVisitorIdentity(visitorIdentityData: VisitorIdentityProof): Future[Int] = {
    visitorIdentityProofRepository.create(visitorIdentityData)
  }



  def list(): Future[Seq[Visitor]] = visitorRepository.list()

  def getVisitorById(id: Int): Future[Option[Visitor]] = visitorRepository.getVisitorById(id)

  def getVisitorByEmail(mail: String): Future[Option[Visitor]] = visitorRepository.getVisitorByEmail(mail)

}
