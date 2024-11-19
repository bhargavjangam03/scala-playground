package scheduler

import service.GuestService

import java.time.{LocalDateTime, ZoneId}
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import javax.inject.{Inject, Singleton}

@Singleton
class RestaurantMailServiceScheduler @Inject()(guestService: GuestService) {

  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  // Calculate the delay until the next 8 AM
  private def calculateDelayUntil8AM: Long = {
    val now = LocalDateTime.now(ZoneId.systemDefault())
    val eightAMToday = now.withHour(8).withMinute(0).withSecond(0).withNano(0)

    val delay = if (now.isBefore(eightAMToday)) {
      // If it's before 8 AM today, schedule for today
      java.time.Duration.between(now, eightAMToday).toMillis
    } else {
      // If it's after 8 AM today, schedule for 8 AM tomorrow
      val eightAMTomorrow = eightAMToday.plusDays(1)
      java.time.Duration.between(now, eightAMTomorrow).toMillis
    }

    delay
  }

  // Schedule the task to run daily at 8 AM
  private val initialDelay = calculateDelayUntil8AM
  private val period = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

  // Schedule the cron job to run daily at 8 AM
  scheduler.scheduleAtFixedRate(
    () => guestService.fetchGuestListAndSendMenu(),
    initialDelay,       // Delay until next 8 AM
    period,             // Repeat every 24 hours
    TimeUnit.MILLISECONDS
  )

}
