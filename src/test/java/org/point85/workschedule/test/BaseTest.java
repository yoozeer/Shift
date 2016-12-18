package org.point85.workschedule.test;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.point85.workschedule.BreakPeriod;
import org.point85.workschedule.NonWorkingPeriod;
import org.point85.workschedule.Shift;
import org.point85.workschedule.ShiftInstance;
import org.point85.workschedule.Team;
import org.point85.workschedule.WorkSchedule;

abstract class BaseTest {
	protected void printTeams(List<Team> teams) throws Exception {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		float teamPercent = 0.0f;
		int count = 1;
		for (Team team : teams) {
			teamPercent += team.getPercentageWorked();
			System.out.println("(" + count + ")" + " Team: " + team.getName() + " " + team.getDescription()
					+ ", rotation duration: " + team.getRotationDuration() + ", scheduled working time: "
					+ team.getWorkingTime() + ", percentage worked: " + df.format(team.getPercentageWorked())
					+ "%, days in rotation: " + team.getShiftRotation().getDays() + ", avg hours worked per week: "
					+ team.getHoursWorkedPerWeek() + ", rotation start: " + team.getRotationStart());
			count++;
		}
		System.out.println("Total team coverage: " + df.format(teamPercent) + "%");
	}

	protected void printShifts(List<Shift> shifts) throws Exception {

		System.out.println("Shifts: ");
		for (Shift shift : shifts) {
			System.out.println("   Name: " + shift.getName() + "(" + shift.getDescription() + "), start: "
					+ shift.getStart() + ", end: " + shift.getEnd());

			System.out.println("      Breaks: ");

			for (BreakPeriod breakPeriod : shift.getBreaks()) {
				System.out.println("      Name: " + breakPeriod.getName() + "(" + breakPeriod.getDescription()
						+ "), start: " + breakPeriod.getStart() + ", end: " + breakPeriod.getEnd());
			}
		}
	}

	protected void printWorkSchedule(WorkSchedule workSchedule) throws Exception {
		System.out.println("Work schedule rotation duration: " + workSchedule.getRotationDuration()
				+ ", scheduled working time " + workSchedule.getWorkingTime());

		System.out.println("Non-working periods:");

		Duration totalMinutes = Duration.ofMinutes(0);

		for (NonWorkingPeriod period : workSchedule.getNonWorkingPeriods()) {
			totalMinutes = totalMinutes.plusMinutes(period.getDuration().toMinutes());

			System.out.println("   Name: " + period.getName() + " (" + period.getDescription() + "), Start: "
					+ period.getStartDateTime() + ", Duration: " + period.getDuration());
		}
		System.out.println("Total non-working time: " + totalMinutes);
		
		// shifts
		printShifts(workSchedule.getShifts());
	}

	protected void printShiftInstances(WorkSchedule workSchedule, LocalDate start) throws Exception {
		LocalDate day = start;
		int days = workSchedule.getTeams().get(0).getShiftRotation().getDays();

		for (int i = 0; i < days; i++) {
			System.out.println("[" + (i + 1) + "] Shifts for day: " + day);

			List<ShiftInstance> instances = workSchedule.getShiftInstancesForDay(day);

			if (instances.size() == 0) {
				System.out.println("   No working shifts");
			} else {
				int count = 1;
				for (ShiftInstance instance : instances) {
					System.out.println("   (" + count + ")" + " Team: " + instance.getTeam().getName() + " "
							+ instance.getTeam().getDescription() + ", shift: " + instance.getShift().getName()
							+ ", start: " + instance.getShift().getStart() + ", end: " + instance.getShift().getEnd());
					count++;
				}
			}
			day = day.plusDays(1);
		}
	}
}
