import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PayrollSystem {

    static Scanner sc = new Scanner(System.in);

    static class Employee {
        String employeeNumber;
        String name;
        String birthday;
        double hourlyRate;

        /*
        3D Attendance Structure: 
        > Dimension 1: Month index (1–12)
        > Dimension 2: Cutoff index (0 = 1st cutoff, 1 = 2nd cutoff)
        > Dimension 3: Dynamic time entries
        (Index 0 of month is intentionally unused for clarity.)
        */
        double[][][] attendanceIn = new double[13][2][];
        double[][][] attendanceOut = new double[13][2][];

        Employee(String employeeNumber, String name, String birthday, double hourlyRate) {
            this.employeeNumber = employeeNumber;
            this.name = name;
            this.birthday = birthday;
            this.hourlyRate = hourlyRate;
        }
    }

    // Global in-memory Employee Repository
    static Employee[] employees;

    public static void main(String[] args) {
        // Data Ingestion Phase
        loadEmployeesFromCSV("mph_employees_record.csv");
        loadAttendanceFromCSV("attendance_record.csv");

        // Basic Authentication Mechanism
        System.out.println("=========== MotorPh Login =============");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        
        // Access Control Validatin: "payroll_staff" OR "employee" are the ONLY Valid Usernames Sharing the Same Password (12345)
        if (!(password.equals("12345") &&
                (username.equals("payroll_staff") || username.equals("employee")))) {
            System.out.println("Incorrect username and/or password!");
            return;
        }

        if (username.equals("employee")) {
            employeeMenu();
        } else {
            payrollMenu();
        }
    }

    
    /*
    * CSV Employees Loader Responsibilities 
    > Parse structured employee data
    > Convert to Employee objects via parseEmployee()
    > Store in memory
    */
    static void loadEmployeesFromCSV(String mph_employees_record) {
    ArrayList<Employee> list = new ArrayList<>();

    // try-with-resources Ensures Automatic File Closure Preventing Resource Leaks
        try (BufferedReader br = new BufferedReader(new FileReader(mph_employees_record))) {

            // Read header row to dynamically locate the "Hourly Rate" column index
            String headerLine = br.readLine();
            int rateColumnIndex = findColumnIndex(headerLine, "Hourly Rate");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Delegate CSV line parsing and object construction to parseEmployee()
                Employee emp = parseEmployee(line, rateColumnIndex);
                list.add(emp);
            }

        } catch (IOException e) {
            System.out.println("Error reading employee file: " + e.getMessage());
        }

        // Converts Dynamic Array List to a Fixed Sized Array to Stabilize Data Structure Post-Loading
        employees = list.toArray(new Employee[0]);
    }

    /*
    Column Index Resolver: Scans the CSV Header Row for a Named Column
    > Returns the matching index if found
    > Falls back to default index 18 if the column name is not matched
    */
    static int findColumnIndex(String headerLine, String columnName) {
        String[] headers = headerLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return 18; // Default fallback if column header is not found
    }

    /*
    Employee Object Factory: Parses a Single CSV Line and Constructs an Employee
    > Accepts the rate column index dynamically to avoid hardcoded positions
    > Data contract assumption:
    > Column 0: Employee Number
    > Column 1: Last Name
    > Column 2: First Name
    > Column 3: Birthday
    > Column rateColumnIndex: Hourly Rate
    */
    static Employee parseEmployee(String line, int rateColumnIndex) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String empNo = parts[0].trim();
        String lastName = parts[1].trim();
        String firstName = parts[2].trim();
        String birthday = parts[3].trim();
        double rate = Double.parseDouble(parts[rateColumnIndex].trim().replace(",", ""));

        return new Employee(empNo, firstName + " " + lastName, birthday, rate); 
}

    // CSV Attendance Loader: Requires Employee Numbers to Match those in employees_record.csv
    static void loadAttendanceFromCSV(String attendance_record) {
        try (BufferedReader br = new BufferedReader(new FileReader(attendance_record))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");

                /*
                Expected Structure:
                > 0 = Employee #
                > 3 = Date (MM/DD/YYYY)
                > 4 = Time In
                > 5 = Time Out
                */
                String empNo = parts[0].trim();
                String date = parts[3].trim(); // Format: MM/DD/YYYY
                // Note: BigDecimal would provide higher arithmetic precision here
                double timeIn = Double.parseDouble(parts[4].trim().replace(":", "."));
                double timeOut = Double.parseDouble(parts[5].trim().replace(":", "."));

                Employee emp = findEmployee(empNo);
                // Defensive Check for Orphan Attendance Records
                if (emp == null) continue;

                // Extract Month and Day via Substring Parsing 
                int month = Integer.parseInt(date.substring(0, 2));
                int day = Integer.parseInt(date.substring(3, 5));
                // Cutoff Logic
                int cutoff = (day <= 15) ? 0 : 1;

                emp.attendanceIn[month][cutoff] = append(emp.attendanceIn[month][cutoff], timeIn);
                emp.attendanceOut[month][cutoff] = append(emp.attendanceOut[month][cutoff], timeOut);
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance file.");
        }
    }

    // Dynamic Array Expansion Utility: We Manually Created a New Larger Array and copied the Values due to Java's Fixed Array Size 
    static double[] append(double[] array, double value) {
        if (array == null) return new double[]{value};
        double[] newArr = new double[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = value;
        return newArr;
    }

    
    // Processing Logic
    static void processPayroll(Employee emp) {
        String[] monthNames = {"", "January", "February", "March", "April", "May", "June", 
                               "July", "August", "September", "October", "November", "December"};

        System.out.println("========================================");
        System.out.println("PAYROLL REPORT FOR: " + emp.name);
        System.out.println("Employee Number: " + emp.employeeNumber);
        System.out.println("Birthday: " + emp.birthday);
        System.out.println("========================================");

        // Loop from June (6) to December (12)
        for (int m = 6; m <= 12; m++) {
            double firstHours = computeHoursWorked(emp.attendanceIn[m][0], emp.attendanceOut[m][0]);
            double secondHours = computeHoursWorked(emp.attendanceIn[m][1], emp.attendanceOut[m][1]);

            // Skips Months with No Data
            if (firstHours == 0 && secondHours == 0) continue; 

            double firstGross = firstHours * emp.hourlyRate;
            double secondGross = secondHours * emp.hourlyRate;
            double combinedGross = firstGross + secondGross;

            double sss = computeSSS(combinedGross);
            double philHealth = computePhilHealth(combinedGross);
            double pagIbig = computePagibig(combinedGross);
            double tax = computeIncomeTax(combinedGross - (sss + philHealth + pagIbig));
            double totalDeductions = sss + philHealth + pagIbig + tax;

System.out.println("Payroll Summary for: " + monthNames[m]);

System.out.println("[ First Cutoff: 1 - 15 ]");
System.out.println("Hours Worked: " + firstHours);
System.out.println("Gross Pay: " + firstGross);
System.out.println("Net Pay: " + firstGross);

System.out.println("[ Second Cutoff: 16 - 30 ]");
System.out.println("Hours Worked: " + secondHours);
System.out.println("Gross Pay: " + secondGross);
System.out.println("Net Pay: " + (secondGross - totalDeductions));

System.out.println("=========== Deductions =============");
System.out.println("SSS: " + sss);
System.out.println("PhilHealth: " + philHealth);
System.out.println("Pag-IBIG: " + pagIbig);
System.out.println("Tax: " + tax);
System.out.println("Total: " + totalDeductions);
System.out.println("========================================");

        }
    }

   
    // Time Conversion Utility: Converts HH.MM Representation into Decimal Hours
    static double convertToHours(double timeValue) {
        int hour = (int) timeValue;
        double decimal = timeValue - hour;
        int minutes = (int) (decimal * 100);
        return hour + (minutes / 60.0);
    }

    static double computeHoursWorked(double[] timeIn, double[] timeOut) {
        double hoursWorked = 0;
        if (timeIn == null || timeOut == null) return 0;
        for (int i = 0; i < timeIn.length; i++) {
            double in = timeIn[i];
            double out = timeOut[i];
            // Apply 5-Minute Grace Period (8:05 or Earlier Counts as 8:00)
            if (in <= 8.05) in = 8.0;
            // Apply Strict 8:00 AM to 5:00 PM (17.0) Window
            if (in < 8.0) in = 8.0;
            if (out > 17.0) out = 17.0;
            double decimalIn = convertToHours(in);
            double decimalOut = convertToHours(out);
            double daily = decimalOut - decimalIn;
            // (-) Negative Time Proctetion 
            if (decimalOut < decimalIn || daily < 0) daily = 0;
            hoursWorked += daily;
        }
        return hoursWorked;
    }

    static Employee findEmployee(String empNo) {
        for (Employee emp : employees) {
            if (emp.employeeNumber.equals(empNo)) return emp;
        }
        return null;
    }

    // Employee Portal: Access Limited to the Viewing of Personal Detiails Only
    static void employeeMenu() {
        System.out.println("=========== Select an Option =============");
        System.out.println("1. Enter Employee Number");
        System.out.println("2. Exit The Program");
        System.out.print("Enter Option: ");
        int choice = sc.nextInt();
        sc.nextLine();
        if (choice == 2) return;
        System.out.println("========================================");
        System.out.print("Enter Employee Number: ");
        String empNo = sc.nextLine();
        Employee emp = findEmployee(empNo);
        if (emp == null) {
            System.out.println("========================================");
            System.out.println("Employee Number Does Not Exist!");
            return;
        }
        System.out.println("=========== Employee Number Information =============");
        System.out.println("Employee Number: " + emp.employeeNumber);
        System.out.println("Name: " + emp.name);
        System.out.println("Birthday: " + emp.birthday);
    }

    // Payroll Staff Portal: Provides Individual and Batch Payroll Processing
    static void payrollMenu() {
        System.out.println("=========== Select an Option =============");
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit the program");
        System.out.print("Enter Option: ");
        int choice = sc.nextInt();
        if (choice == 2) return;
        System.out.println("=========== Select an Option =============");
        System.out.println("1. One employee");
        System.out.println("2. All employees");
        System.out.println("3. Exit the program");
        System.out.print("Enter Option: ");
        int subChoice = sc.nextInt();
        sc.nextLine();
        if (subChoice == 3) return;
        if (subChoice == 1) {
            System.out.println("========================================");
            System.out.print("Enter Employee Number: ");
            String empNo = sc.nextLine();
            Employee emp = findEmployee(empNo);
            if (emp == null) {
                System.out.println("========================================");
                System.out.println("Employee Number Does Not Exist");
                return;
            }
            processPayroll(emp);
        } else if (subChoice == 2) {
            for (Employee emp : employees) {
                processPayroll(emp);
            }
        }
    }

    // SSS Monthly Deduction Table as of 2024
    static double computeSSS(double monthlyGross) {
        double monthlyContribution = 0;
        
        if (monthlyGross < 3250) monthlyContribution = 135;
        else if (monthlyGross <= 3750) monthlyContribution = 157.5;
        else if (monthlyGross <= 4250) monthlyContribution = 180;
        else if (monthlyGross <= 4750) monthlyContribution = 202.5;
        else if (monthlyGross <= 5250) monthlyContribution = 225;
        else if (monthlyGross <= 5750) monthlyContribution = 247.5;
        else if (monthlyGross <= 6250) monthlyContribution = 270;
        else if (monthlyGross <= 6750) monthlyContribution = 292.5;
        else if (monthlyGross <= 7250) monthlyContribution = 315;
        else if (monthlyGross <= 7750) monthlyContribution = 337.5;
        else if (monthlyGross <= 8250) monthlyContribution = 360;
        else if (monthlyGross <= 8750) monthlyContribution = 382.5;
        else if (monthlyGross <= 9250) monthlyContribution = 405;
        else if (monthlyGross <= 9750) monthlyContribution = 427.5;
        else if (monthlyGross <= 10250) monthlyContribution = 450;
        else if (monthlyGross <= 10750) monthlyContribution = 472.5;
        else if (monthlyGross <= 11250) monthlyContribution = 495;
        else if (monthlyGross <= 11750) monthlyContribution = 517.5;
        else if (monthlyGross <= 12250) monthlyContribution = 540;
        else if (monthlyGross <= 12750) monthlyContribution = 562.5;
        else if (monthlyGross <= 13250) monthlyContribution = 585;
        else if (monthlyGross <= 13750) monthlyContribution = 607.5;
        else if (monthlyGross <= 14250) monthlyContribution = 630;
        else if (monthlyGross <= 14750) monthlyContribution = 652.5;
        else if (monthlyGross <= 15250) monthlyContribution = 675;
        else if (monthlyGross <= 15750) monthlyContribution = 697.5;
        else if (monthlyGross <= 16250) monthlyContribution = 720;
        else if (monthlyGross <= 16750) monthlyContribution = 742.5;
        else if (monthlyGross <= 17250) monthlyContribution = 765;
        else if (monthlyGross <= 17750) monthlyContribution = 787.5;
        else if (monthlyGross <= 18250) monthlyContribution = 810;
        else if (monthlyGross <= 18750) monthlyContribution = 832.5;
        else if (monthlyGross <= 19250) monthlyContribution = 855;
        else if (monthlyGross <= 19750) monthlyContribution = 877.5;
        else if (monthlyGross <= 20250) monthlyContribution = 900;
        else if (monthlyGross <= 20750) monthlyContribution = 922.5;
        else if (monthlyGross <= 21250) monthlyContribution = 945;
        else if (monthlyGross <= 21750) monthlyContribution = 967.5;
        else if (monthlyGross <= 22250) monthlyContribution = 990;
        else if (monthlyGross <= 22750) monthlyContribution = 1012.5;
        else if (monthlyGross <= 23250) monthlyContribution = 1035;
        else if (monthlyGross <= 23750) monthlyContribution = 1057.5;
        else if (monthlyGross <= 24250) monthlyContribution = 1080;
        else monthlyContribution = 1125;

        return monthlyContribution;
    }

    // PhilHealth Monthly Deduction Calculation
    static double computePhilHealth(double monthlySalary) {
        double monthlyPremium = monthlySalary * 0.03;
        if (monthlyPremium > 1800) monthlyPremium = 1800;
        return monthlyPremium * 0.5;
    }

    // PagIbig Monthly Deduction Calculation
    static double computePagibig(double monthlySalary) {
        double rate = (monthlySalary > 1500) ? 0.04 : (monthlySalary >= 1000 ? 0.03 : 0);
        double contribution = monthlySalary * rate;
        return Math.min(contribution, 100);
    }

    // Income Tax Monthly Deduction Calculation
    static double computeIncomeTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0;
        else if (taxableIncome <= 33332) return (taxableIncome - 20833) * 0.20;
        else if (taxableIncome <= 66666) return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome <= 166666) return 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome <= 666666) return 40833.33 + (taxableIncome - 166667) * 0.32;
        else return 200833.33 + (taxableIncome - 666667) * 0.35;
    }
}
