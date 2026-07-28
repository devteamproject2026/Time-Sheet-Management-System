package com.tms.businessservice.entity;


	import java.time.LocalDate;
	import java.time.LocalTime;

	import jakarta.persistence.*;

	@Entity
	@Table(name = "attendance")
	public class Attendance {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "attendance_id")
	    private int attendanceId;


	    @Column(name = "employee_id")
	    private int employeeId;


	    @Column(name = "attendance_date")
	    private LocalDate attendanceDate;


	    @Column(name = "check_in")
	    private LocalTime checkIn;


	    @Column(name = "check_out")
	    private LocalTime checkOut;


	    @Column(name = "status")
	    private String status;

        
	    public Attendance() {
			super();
			// TODO Auto-generated constructor stub
		}

	    
	    
		public Attendance(int attendanceId, int employeeId, LocalDate attendanceDate, LocalTime checkIn,
				LocalTime checkOut, String status) {
			super();
			this.attendanceId = attendanceId;
			this.employeeId = employeeId;
			this.attendanceDate = attendanceDate;
			this.checkIn = checkIn;
			this.checkOut = checkOut;
			this.status = status;
		}



		public int getAttendanceId() {
	        return attendanceId;
	    }

	    public void setAttendanceId(int attendanceId) {
	        this.attendanceId = attendanceId;
	    }


	    public int getEmployeeId() {
	        return employeeId;
	    }

	    public void setEmployeeId(int employeeId) {
	        this.employeeId = employeeId;
	    }


	    public LocalDate getAttendanceDate() {
	        return attendanceDate;
	    }

	    public void setAttendanceDate(LocalDate attendanceDate) {
	        this.attendanceDate = attendanceDate;
	    }


	    public LocalTime getCheckIn() {
	        return checkIn;
	    }

	    public void setCheckIn(LocalTime checkIn) {
	        this.checkIn = checkIn;
	    }


	    public LocalTime getCheckOut() {
	        return checkOut;
	    }

	    public void setCheckOut(LocalTime checkOut) {
	        this.checkOut = checkOut;
	    }


	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String status) {
	        this.status = status;
	    }
	    
	    @Override
	    public String toString() {
	        return "Attendance{" +
	                "attendanceId=" + attendanceId +
	                ", employeeId=" + employeeId +
	                ", attendanceDate=" + attendanceDate +
	                ", checkIn=" + checkIn +
	                ", checkOut=" + checkOut +
	                ", status='" + status + '\'' +
	                '}';
	    }
	}

