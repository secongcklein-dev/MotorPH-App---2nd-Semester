import java.util.Scanner;

/*
Employee class which represents a single employee entity.
 
It stores personal details and handles salary computations.
*/
class Employee {

// Basic employee information.
int employeeNum;
String firstName;
String lastName;
String birthday;

// Time records for 5 working days (Monday to Friday assumption).
double[] timeIn = new double[5];
double[] timeOut = new double[5];
double breakHours = 1.0;

// Payroll related fields.
double hoursWorked;
double hourlyRate;
double grossSalary;
double sss;
double philHealth;
double pagIbig;
double tax;
double totalDeductions;
double netSalary;

/*
Computes total weekly hours worked by: (timeOut - timeIn) - breakHours.

Negative values are prevented to avoid invalid inputs from producing incorrect totals.
*/
void computeHoursWorked() {
hoursWorked = 0;
for (int i = 0; i < 5; i++) {double dailyHours = (timeOut[i] - timeIn[i]) - breakHours;
if (dailyHours < 0) {dailyHours = 0;}
hoursWorked += dailyHours;}
}

// Gross salary computation.
void computeGrossSalary() {grossSalary = hoursWorked * hourlyRate;}
double getGrossSalary() {return grossSalary;}


// SSS deduction calculation.
double getSSS() {
double monthlyGross = getGrossSalary() * 4.33;

if (monthlyGross < 3250) return 135.0 / 4.33;
else if (monthlyGross >= 24750) return 1125.0 / 4.33;
else {
double[][] sssTable = {
{3250, 135}, {3750, 157.5}, {4250, 180}, {4750, 202.5}, {5250, 225},
{5750, 247.5}, {6250, 270}, {6750, 292.5}, {7250, 315}, {7750, 337.5},
{8250, 360}, {8750, 382.5}, {9250, 405}, {9750, 427.5}, {10250, 450},
{10750, 472.5}, {11250, 495}, {11750, 517.5}, {12250, 540}, {12750, 562.5},
{13250, 585}, {13750, 607.5}, {14250, 630}, {14750, 652.5}, {15250, 675},
{15750, 697.5}, {16250, 720}, {16750, 742.5}, {17250, 765}, {17750, 787.5},
{18250, 810}, {18750, 832.5}, {19250, 855}, {19750, 877.5}, {20250, 900},
{20750, 922.5}, {21250, 945}, {21750, 967.5}, {22250, 990}, {22750, 1012.5},
{23250, 1035}, {23750, 1057.5}, {24250, 1080}
};

for (double[] entry : sssTable) {

if (monthlyGross <= entry[0]) return entry[1] / 4.33;

}
}
return 0;
}

// PhilHealth deduction calculation.
double getPhilHealth() {
return (getGrossSalary() * 4.33 * 0.02) / 4.33;
}

// Pag-Ibig deduction calculation
double getPagIbig() {
double monthlyGross = getGrossSalary() * 4.33;
double contribution = (monthlyGross <= 1500) ? monthlyGross * 0.01 : monthlyGross * 0.02;

if (contribution > 100) contribution = 100;

return contribution / 4.33;
}

// Tax deduction calculation.
double getTax() {
double monthlyGross = getGrossSalary() * 4.33;
double monthlySSS = getSSS() * 4.33;
double monthlyPhilHealth = getPhilHealth() * 4.33;
double monthlyPagIbig = getPagIbig() * 4.33;
double taxableIncome = monthlyGross - (monthlySSS + monthlyPhilHealth + monthlyPagIbig);
double tax = 0;

if (taxableIncome <= 20832) tax = 0;
else if (taxableIncome <= 33332) tax = (taxableIncome - 20833) * 0.20;
else if (taxableIncome <= 66666) tax = 2500 + (taxableIncome - 33333) * 0.25;
else if (taxableIncome <= 166666) tax = 10833 + (taxableIncome - 66667) * 0.30;
else if (taxableIncome <= 666666) tax = 40833.33 + (taxableIncome - 166667) * 0.32;
else tax = 200833.33 + (taxableIncome - 666667) * 0.35;

return tax / 4.33;
}

// Total deductions calculations.
void computeDeductions() {
sss = getSSS();
philHealth = getPhilHealth();
pagIbig = getPagIbig();
tax = getTax();
totalDeductions = sss + philHealth + pagIbig + tax;
}

//Net salary calculation: grossSalary - totalDeductions
void computeNetSalary() {netSalary = grossSalary - totalDeductions;}
}


public class MS2 {
public static void main(String[] args) {

Scanner sc = new Scanner(System.in);

Employee emp1 = new Employee();
emp1.employeeNum = 10001;
emp1.firstName = "Manuel";
emp1.lastName = "Garcia III";
emp1.birthday = "10/11/1983";
emp1.hourlyRate = 535.71;

Employee emp2 = new Employee();
emp2.employeeNum = 10002;
emp2.firstName = "Antonio";
emp2.lastName = "Lim";
emp2.birthday = "06/19/1988";
emp2.hourlyRate = 357.14;

Employee[] employees = {emp1, emp2};

// Login section: The authentication gate before system access.
System.out.println("===== LOGIN =====");
System.out.print("Username: ");
String username = sc.nextLine();
System.out.print("Password: ");
int password = sc.nextInt();
sc.nextLine();

/*
Validate credentials.

Only two valid usernames allowed sharing the same password.
*/
if (!(username.equals("employee") || username.equals("payroll_staff")) || password != 12345) 
{System.out.println("Incorrect username and/or password.");
sc.close();
return;
}

// Employee Access: One can only view personal details due to access limitation.
if (username.equals("employee")) {

System.out.println("1. Enter Employee Number");
System.out.println("2. Exit The Program");
System.out.print("Choose option: ");
int choice = sc.nextInt();

if (choice == 1) {
System.out.print("Enter Employee Number: ");
int inputNumber = sc.nextInt();

boolean found = false;

for (int i = 0; i < employees.length; i++) {
if (employees[i].employeeNum == inputNumber) {
System.out.println("Employee Number: " + employees[i].employeeNum);
System.out.println("Employee Name: " + employees[i].firstName + " " + employees[i].lastName);
System.out.println("Birthday: " + employees[i].birthday);
found = true;
break;
}
}

if (!found) {System.out.println("Employee Number Does Not Exist");
}

sc.close();
return;

} else {sc.close();
return;
}
}

// Payroll staff access: can execute full payroll coimputation.
if (username.equals("payroll_staff")) {

System.out.println("===== MOTORPH PAYROLL SYSTEM =====");

/*
Input time records per employee.
5 working days assumed.
*/
for (int e = 0; e < employees.length; e++) {

System.out.println("Enter time records for Employee #: " + employees[e].employeeNum + " - " + employees[e].firstName + " " + employees[e].lastName);

for (int d = 0; d < 5; d++) {
System.out.print("Day " + (d + 1) + " Time In: ");
employees[e].timeIn[d] = sc.nextDouble();
System.out.print("Day " + (d + 1) + " Time Out: ");
employees[e].timeOut[d] = sc.nextDouble();
}

// Sequential payroll processing which follows program flow strictly.
employees[e].computeHoursWorked();
employees[e].computeGrossSalary();
employees[e].computeDeductions();
employees[e].computeNetSalary();
}

 //Final payroll summary display.
System.out.println("===== PAYROLL SUMMARY =====");

for (int e = 0; e < employees.length; e++) {
System.out.println("Employee Number: " + employees[e].employeeNum);
System.out.println("Employee Name: " + employees[e].firstName + " " + employees[e].lastName);
System.out.println("Birthday: " + employees[e].birthday);
System.out.println("Hours Worked: " + employees[e].hoursWorked);
System.out.println("Hourly Rate: " + employees[e].hourlyRate);
System.out.println("Gross Salary: " + employees[e].grossSalary);
System.out.println("SSS: " + employees[e].sss);
System.out.println("PhilHealth: " + employees[e].philHealth);
System.out.println("Pag-IBIG: " + employees[e].pagIbig);
System.out.println("Withholding Tax: " + employees[e].tax);
System.out.println("Total Deductions: " + employees[e].totalDeductions);
System.out.println("Net Salary: " + employees[e].netSalary);
System.out.println("===============================");
}
}

sc.close();
}

}