BE Developer (Java) Take-home Assignment
Task - Balance Module Challenge
Note
The project does not need to be fully runnable. We will focus on reviewing the correctness of the logic and design, rather than execution details.
To do
Initialize a new Spring Boot project with Hibernate.
Use MySQL or PostgreSQL.
Create a Balance module.
In BalanceService, create a function to issue transactions.
Ensure the function is concurrency-safe.
Support an option called checkBalance. if it’s true, negative balances are not allowed.
Support multiple transactions to different users in a single call.
Every transaction should have a column called endingBalance to record the balance after this transaction.
Provide a RESTful API to issue transactions in BalanceController.
The request body should include a list of transactions, each containing userId and amount (the amount can be positive or negative).
Provide a RESTful API to get the balance of a specified user in BalanceController.
Think about
Is your code efficient enough?
Is your code readable enough?
Submission
After completion, please upload your code to GitHub and send the repository URL to your contact person.
