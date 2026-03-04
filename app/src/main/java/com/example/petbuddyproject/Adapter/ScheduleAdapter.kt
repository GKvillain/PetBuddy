//package com.example.newscheduleact
package com.example.petbuddyproject.Adapter
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newscheduleact.databinding.ItemScheduleCalendarBinding
import com.example.newscheduleact.databinding.ItemScheduleContainerBinding
import com.example.newscheduleact.databinding.ItemScheduleHeaderBinding
import com.example.newscheduleact.databinding.ItemScheduleMonthHeaderBinding
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import kotlin.collections.map

private val dayFormatter = DateTimeFormatter.ofPattern("d")
private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EE", Locale.ENGLISH)
private val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

class ScheduleAdapter (
    private val items : MutableList<ScheduleUiItem>,
    private val listener : ScheduleActionListener,
    private val onDateSelected: (CalendarDate) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    companion object{
        const val TYPE_CALENDAR = 0
        const val TYPE_SCHEDULE_HEADER = 1
        const val TYPE_MONTH_HEADER = 2
        const val TYPE_SCHEDULE = 3
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_CALENDAR -> CalendarViewHolder(
                ItemScheduleCalendarBinding.inflate(inflater, parent, false),
                onDateSelected
            )
            TYPE_SCHEDULE_HEADER -> ScheduleHeaderViewHolder(ItemScheduleHeaderBinding.inflate(inflater,parent,false))
            TYPE_MONTH_HEADER -> MonthHeaderViewHolder(
                ItemScheduleMonthHeaderBinding.inflate(inflater, parent, false)
            )

            TYPE_SCHEDULE -> ScheduleContainerViewHolder(
                ItemScheduleContainerBinding.inflate(inflater, parent, false),
                listener
            )

            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is CalendarViewHolder -> {

                val events = items
                    .filterIsInstance<ScheduleUiItem.Schedule>()
                    .mapNotNull { schedule ->
                        schedule.startTime?.let { timestamp ->

                            val localDate = timestamp
                                .toDate()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            ScheduleEvent(
                                date = CalendarDate(
                                    localDate
                                        .atStartOfDay(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                                ),
                                tagColor = schedule.tag.tagColor
                            )
                        }
                    }

                holder.bind(events)
            }
            is ScheduleHeaderViewHolder -> holder.bind()
            is MonthHeaderViewHolder -> holder.bind(items[position] as ScheduleUiItem.MonthHeader)
            is ScheduleContainerViewHolder -> holder.bind(items[position] as ScheduleUiItem.Schedule,position)

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]){
            is ScheduleUiItem.Calendar -> TYPE_CALENDAR
            is ScheduleUiItem.UpcomingHeader -> TYPE_SCHEDULE_HEADER
            is ScheduleUiItem.MonthHeader -> TYPE_MONTH_HEADER
            is ScheduleUiItem.Schedule -> TYPE_SCHEDULE
        }
    }

    //ViewHolder
    class CalendarViewHolder(
        private val binding : ItemScheduleCalendarBinding,
        private val onDateSelected: (CalendarDate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.calendarView.setupCalendar(
                selectionMode = CalendarView.SelectionMode.RANGE
            )
            binding.calendarView.onDateClickListener = { date ->
                onDateSelected(date)
            }
        }
        fun bind(
            events: List<ScheduleEvent>
        ){

            binding.calendarView.datesIndicators = events.map {
                object : CalendarView.DateIndicator{
                    override val date = it.date
                    override val color = it.tagColor
                }
            }
        }
    }

    class ScheduleHeaderViewHolder(
        private val binding: ItemScheduleHeaderBinding,

    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvUpSchedule
        }
    }

    class MonthHeaderViewHolder(
        private val binding : ItemScheduleMonthHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: ScheduleUiItem.MonthHeader) {
        val monthName = Month.of(item.month)
            .getDisplayName(TextStyle.FULL, Locale.ENGLISH)

        binding.tvMonthHeader.text = "$monthName ${item.year}"
        }
    }

    class ScheduleContainerViewHolder(
        private val binding: ItemScheduleContainerBinding,
        private val listener: ScheduleActionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleUiItem.Schedule, position: Int) {

            // ---------- Title ----------
            binding.tvScheduleTitle.text = item.title

            // ---------- Time ----------
            if (item.isAllDay) {
                binding.tvTime.text = "All day"
            } else if (item.startTime != null && item.endTime != null) {

                val start = item.startTime
                    .toDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

                val end = item.endTime
                    .toDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

                binding.tvTime.text =
                    "${start.format(timeFormatter)} - ${end.format(timeFormatter)}"
            } else {
                binding.tvTime.text = ""
            }

            // ---------- Date circle ----------
            item.startTime?.let {
                val dateTime = it
                    .toDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

                binding.tvDay.text = dateTime.format(dayFormatter)
                binding.tvDayOfWeek.text = dateTime
                    .format(dayOfWeekFormatter)
                    .uppercase()
            }

            binding.scheduleCard.backgroundTintList =
                ColorStateList.valueOf(item.tag.tagColor)
            binding.root.setOnClickListener {
                listener.onScheduleClick(position)
            }
        }
    }






}
