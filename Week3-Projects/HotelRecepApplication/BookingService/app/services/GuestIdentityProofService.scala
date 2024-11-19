package services

import models.GuestIdentityProof
import repositories.GuestIdentityProofRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GuestIdentityProofService @Inject()(guestIdentityProofRepository: GuestIdentityProofRepository)(implicit ec: ExecutionContext) {

  // Method to create a new guest identity proof record and return the generated ID
  def create(visitorIdentity: GuestIdentityProof): Future[Int] = {
    guestIdentityProofRepository.create(visitorIdentity)
  }

  // Method to list all guest identity proof records
  def list(): Future[Seq[GuestIdentityProof]] = {
    guestIdentityProofRepository.list()
  }

  // Method to get a guest identity proof by ID
  def getById(id: Int): Future[Option[GuestIdentityProof]] = {
    guestIdentityProofRepository.getById(id)
  }

  // Method to get a guest identity proof by guestId
  def getByGuestId(guestId: Int): Future[Option[GuestIdentityProof]] = {
    guestIdentityProofRepository.getById(guestId)
  }
}
