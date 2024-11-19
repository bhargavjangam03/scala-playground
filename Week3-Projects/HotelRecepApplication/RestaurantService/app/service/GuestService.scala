package service

import repository._

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import utils.MailUtil.composeAndSendEmailAllGuests

@Singleton
class GuestService @Inject()(guestRepository: GuestRepository,menuService: MenuService)(implicit ec: ExecutionContext) {

  def fetchGuestListAndSendMenu(): Unit = {
    val today = LocalDate.now() // Get today's date

    val menuFuture = menuService.getFoodItemsByDate(today)
    val guestFuture = guestRepository.getActiveGuests

    // Combine futures and process them
    for {
      menuList <- menuFuture
      activeGuests <- guestFuture
    } yield {
      composeAndSendEmailAllGuests(activeGuests, menuList)
    }
  }

}
