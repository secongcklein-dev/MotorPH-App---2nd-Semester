# MotorPH Payroll System

### Phase 1 – Computer Programming 1 Milestone

---

## Course Information

| Course    | Computer Programming 1 |
| --------- | ---------------------- |
| Professor | Aldrin John Tamayo      |
| Section   | H1101                  |
| Group     | 24                     |

---

## Academic Context Statement

This document is submitted as part of the milestone requirement for Computer Programming 1 under Professor Aldrin John Tamayo.

It presents the complete implementation, structure, and technical documentation of the MotorPH Payroll System (Phase 1), developed in accordance with the approved Project Plan Sheet and defined Phase 1 MotorPh requirements.

This repository serves as the official academic submission of Group 24 for the specified milestone.

---

## Group Information

**Section:** H1101
**Group Number:** 24

**Members:**

* Carreon, Rey Lorenz:

Contributed to the implementation and refinement of the authentication and payroll summary workflow. Engineered the login process requiring username, password, and employee ID to trigger direct payroll summary generation. Led the drafting and application of code modifications based on IT coach and QA feedback. Collaborated in evaluating multiple implementations, consolidating the most effective logic, and integrating the finalized version into the system.

* Ramirez, Enzo Gabriel:

Led the end-to-end development of the MotorPH Payroll System, defining the overall architecture and integrating all core components. Engineered payroll computation logic, CSV data parsing, attendance processing, and deduction calculations. Managed system flow integration, resolved implementation issues, and ensured functional consistency across modules. Performed runtime debugging and error resolution and finalized all project deliverables, including technical documentation and repository structure. Co-reviewed IT coach and QA feedback to guide system improvements.

* Secong, Cklein Sinn Onel:

Designed the system wireframes and visual representations to guide the structure and layout of the MotorPH Payroll System interface. Established the formatting for employee information display and structured the payroll output layout to ensure clear and consistent presentation of payroll data within the program.

* Tabor-Abueg, Keren Jemimah:

Defined the project scope for Phase 1, including the key assumptions and operational constraints guiding the system design. Developed the primary use cases and user flow to outline how users interact with the system, and established the overall program flow to ensure alignment between functional requirements and implementation. 

---

## Project Plan

Project Plan Document: [Click Here to View](https://docs.google.com/document/d/18HEJurqGZna3ZPcoLd6GbF_-5D9kD_OX47OSTbpq3Cc/edit?usp=sharing)

---

## Project Overview

The MotorPH Payroll System is a console-based Java application designed to automate payroll computation using structured employee and attendance records stored in CSV files.

The system performs the following core functions:

* Loads employee master data
* Loads attendance records
* Displays employee information (employee number, name and birthday)
* Computes total hours worked per payroll cutoff
* Calculates gross pay
* Applies statutory deductions
* Computes net pay
* Displays structured payroll summaries

The implementation is deterministic, computation-focused, and modular, aligned with Phase 1 constraints.

---

## Problem Statement

Manual payroll computation introduces:

* Calculation errors
* Inconsistent deduction application
* Inefficient record tracking
* Poor traceability

MotorPH requires a basic internal payroll system that:

* Displays employee information
* Computes salary based on hours worked
* Applies uniform deduction logic
* Produces accurate payroll summaries

Phase 1 limits scope strictly to payroll computation and structured display.

---

## Objectives

1. Implement structured employee data ingestion.
2. Implement attendance-based hour computation.
3. Compute payroll per cutoff period.
4. Apply statutory deduction formulas.
5. Maintain modular, object-oriented design.
6. Ensure deterministic and repeatable output.

---

## System Features

### 1. Authentication Layer

* Role-based login:

  * `payroll_staff`
  * `employee`
* Shared password validation
* Access-based menu routing

### 2. Employee Data Management

* Load employee data from CSV
* Store in memory as `Employee` objects
* Display employee identification details

### 3. Attendance Processing

* Load attendance records from CSV
* Map records to employees
* Store attendance using a structured 3D array

### 4. Payroll Computation

* Hours worked calculation
* Grace period logic
* Lunch break deduction
* Cutoff grouping (1–15, 16–30/31)
* Monthly payroll summary (June–December)

### 5. Deduction Modules

* SSS computation (tier-based)
* PhilHealth computation
* Pag-IBIG computation
* Income tax computation

---

## System Architecture

### High-Level Architecture

```
User (Console Input)
        ↓
Authentication Layer
        ↓
Menu Controller
        ↓
Payroll Processing Engine
        ↓
Deduction Modules
        ↓
Formatted Output
```

---

### Structural Breakdown

| Component         | Responsibility                     |
| ----------------- | ---------------------------------- |
| `PayrollSystem`   | Main controller and execution flow |
| `Employee`        | Data model for employee records    |
| CSV Loaders       | Data ingestion from files          |
| Payroll Processor | Salary and deduction computation   |
| Deduction Methods | Individual statutory calculations  |

---

### Core Class and Responsibilities

#### `PayrollSystem`

* Entry point
* Authentication control
* Menu routing
* Payroll orchestration

#### `Employee`

Encapsulates:

* Employee number
* Name
* Birthday
* Hourly Rate
* Attendance records (3D array)

---

## Technology Stack

| Layer         | Technology                 |
| ------------- | -------------------------- |
| Language      | Java                       |
| Data Storage  | CSV Files                  |
| Interface     | Console-based              |
| Collections   | ArrayList                  |
| File Handling | BufferedReader, FileReader |

---

## Installation Instructions

1. Install Java JDK (version 8 or above).
2. Clone the repository.
3. Ensure the following files exist in the project root:

   * `employees_record.csv`
   * `attendance_record.csv`
4. Compile the program.

---

## How to Run the Program

```
javac PayrollSystem.java
java PayrollSystem
```

Login credentials:

| Username      | Password |
| ------------- | -------- |
| payroll_staff | 12345    |
| employee      | 12345    |

---

## Sample Program Flow

1. System loads employee data.
2. System loads attendance records.
3. User logs in.
4. User selects menu option.
5. Payroll processing executes.
6. Structured payroll summary is displayed.

---

## Code Structure Breakdown

```
PayrollSystem.java
│
├── Employee (static inner class)
├── loadEmployeesFromCSV()
├── loadAttendanceFromCSV()
├── processPayroll()
├── computeHoursWorked()
├── computeSSS()
├── computePhilHealth()
├── computePagibig()
├── computeIncomeTax()
├── employeeMenu()
└── payrollMenu()
```

---

## Data Handling Explanation

### Employee Data

* Loaded from CSV
* Stored in dynamic `ArrayList`
* Converted to fixed array after ingestion

### Attendance Data

3D Structure:

```
attendanceIn[month][cutoff][entries]
attendanceOut[month][cutoff][entries]
```

Dimensions:

1. Month index (1–12)
2. Cutoff index (0 = 1–15, 1 = 16–end)
3. Dynamic time entries

Dynamic expansion handled manually via `append()`.

---

## Programming Concepts Used

### Procedural Design
Program flow is structured using methods to handle specific tasks:
* Employee data loading
* Attendance processing
* Payroll computation
* Deduction calculation
* Menu handling

### Modularization
Code is divided into reusable, focused methods to:
* Simplify debugging
* Improve readability
* Enable stepwise testing

### Data Handling
* Employee and attendance records stored in arrays and ArrayLists
* Dynamic indexing for attendance per month and cutoff
* Structured CSV parsing for input validation

### Input/Output Separation
* User input handled through console prompts
* Payroll summaries displayed in a clear, tabular format
* File operations isolated from computation logic

---

## Exception Handling Strategy

* Try-with-resources for file operations
* IOException catch blocks
* Defensive null checks for attendance records
* Protection against negative time calculations

---

## Input Validation Strategy

* Credential validation
* Employee number existence checks
* Null attendance protection
* Negative hours protection
* Time window enforcement (8:00 AM – 5:00 PM)

---

## Testing Strategy

Manual test cases executed for:

1. Valid and invalid login scenarios
2. Existing and non-existing employee numbers
3. Edge cases:

   * Early login
   * Late logout
   * Zero attendance
4. Deduction bracket validation
5. Batch payroll processing

---

## Quality Assurance Test Case

Quality Assurance Test Case Document: [Click Here to View](https://docs.google.com/spreadsheets/d/1VSY-mk2aJ-P0g1CcugyUndu5Y4PM-Ra-eGnyEJJqwNg/edit?usp=sharing)

---

## Limitations

* Password is hardcoded
* No encryption
* Console-only interface
* Limited to CSV file format
* No persistent database
* Fixed payroll window (June–December)
* No overtime handling
* No holiday or leave computation

---

## Future Improvements

1. Replace CSV with database integration.
2. Implement secure authentication.
3. Add overtime rules.
4. Implement payslip export feature.
5. Add GUI interface.
6. Implement year parameterization.
7. Refactor into multi-class architecture.
8. Add automated unit testing.

---

## Repository Structure

```
/root
│
├── PayrollSystem.java
├── employees_record.csv
├── attendance_record.csv
└── README.md
```

---

## Conclusion

The MotorPH Payroll System implements a basic, computation-focused payroll program in accordance with Phase 1 academic requirements.

Features Demonstrated:

* Accurate presentation of employee information:
  - Employee number
  - Name
  - Birthday
* Deterministic calculation of semi-monthly hours worked
* Computation of gross semi-monthly salary based on hours worked
* Application of government deductions to produce semi-monthly salary
* Simple, modular procedural code for clear data flow and calculations

Overview:
The system provides a functional foundation for Phase 1, fulfilling all initial requirements without the use of object-oriented programming, advanced features, or integrations.

---

## Academic Integrity Statement

This project is an original work submitted by Group 24 from the section of H1101 for the course of Computer Programming 1.

The implementation adheres strictly to the defined scope of Phase 1 and reflects independent academic effort in compliance with institutional integrity policies.
