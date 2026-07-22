

const exp = require('express');
const app = exp();
const mysql = require('mysql2');
const cors = require('cors');



const conn = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "root",
    database: "TimeSheetDB"
})
// const conn =mysql.createConnection({
//     host:"localhost",
//     user:"root",
//     password:"root",
//     password:"root",
//     database:"CDAC"
// })


conn.connect((err) => {
    if (err) {
        console.log("Error connecting to database" + err);
    }
    else {
        console.log("DataBAse connected Successfully");
    }
})


//middleware
app.use(exp.json());

app.use(cors("*"));



const checkLogin = (req, res, next) => {
    const { username, password } = req.body;
    if (!username || !password) {
        return res.send("Username or Password doesnt match ")
    }
    next();
}



/*-----------------------------------------
        Hr Registration                   ||
 ------------------------------------------- */

app.post('/register-hr', (req, res) => {

    const {
        username, password, fname, lname, email, contact } = req.body;

    const sql = `
    INSERT INTO users
    (username,password,fname,lname, email,contact, role, status
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;

    const values = [username, password, fname, lname, email, contact, 'HR_HEAD', 'PENDING'];

    conn.query(sql, values, (err) => {

        if (err) {
            console.log(err);
            return res.status(500).send("Registration Failed");
        }

        res.status(201).send("HR Registration Request Submitted");
    });
});


/*-----------------------------------------
        Hr login                   ||
 ------------------------------------------- */

app.post('/login', (req, res) => {

    const { username, password } = req.body;

    const sql = `SELECT * FROM users WHERE username=? AND password=? AND status='APPROVED'
`;
    const values = [username, password];

    conn.query(sql, values, (err, result) => {

        if (err) {
            console.log("Login Failed", err);
            return res.status(500).json({
                message: "Database Error"
            });
        }
        if (result.length === 1) {

            console.log("Login Successful");

            return res.status(200).json({
                user: {
                    userid: result[0].userid,
                    username: result[0].username,
                    role: result[0].role
                },
                token: "abc123"
            });

        } else {

            return res.status(404).json({
                message: "Username or Password does not match"
            });

        }
    });
});


/*--------------------------
Get All Pending HR Requests |
----------------------------*/


app.get('/pending-hr', (req, res) => {

    const sql = `
    SELECT *
    FROM users
    WHERE role='HR_HEAD'
    AND status='PENDING'
    `;

    conn.query(sql, (err, result) => {

        if (err) {
            return res.status(500).send("Database Error");
        }

        res.json(result);
    });
});


/*-----------------------------------
            Approve HR              |
-------------------------------------*/

app.put('/approve-hr/:id', (req, res) => {

    const id = req.params.id;

    const sql = `
    UPDATE users
    SET status='APPROVED'
    WHERE userid=?
    `;

    conn.query(sql, [id], (err) => {

        if (err) {
            return res.status(500).send("Approval Failed");
        }

        res.send("HR Approved");
    });
});

/*-----------------------------------
           Reject HR             |
-------------------------------------*/

app.put('/reject-hr/:id', (req, res) => {

    const id = req.params.id;

    const sql = `
    UPDATE users
    SET status='REJECTED'
    WHERE userid=?
    `;

    conn.query(sql, [id], (err) => {

        if (err) {
            return res.status(500).send("Rejection Failed");
        }

        res.send("HR Rejected");
    });
});

// -------------------------
//crete manager
//-------------------------
app.post('/create-manager', (req, res) => {
    const { username, password, fname, lname, email, contact } = req.body;

    const sql = `
        INSERT INTO users
        (username, password, fname, lname, email, contact, role, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;

    const values = [
        username,
        password,
        fname,
        lname,
        email,
        contact,
        'MANAGER',
        'APPROVED'
    ];

    conn.query(sql, values, (err) => {
        if (err) {
            console.log(err);
            return res.status(500).send("Manager Creation Failed");
        }

        res.status(201).send("Manager Created Successfully");
    });
});




//create Employe
app.post('/create-employee', (req, res) => {
    const { username, password, fname, lname, email, contact } = req.body;

    const sql = `
        INSERT INTO users
        (username, password, fname, lname, email, contact, role, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;

    const values = [
        username,
        password,
        fname,
        lname,
        email,
        contact,
        'EMPLOYEE',
        'APPROVED'
    ];

    conn.query(sql, values, (err) => {
        if (err) {
            console.log(err);
            return res.status(500).send("Employee Creation Failed");
        }

        res.status(201).send("Employee Created Successfully");
    });
});

//------------------------------
 //Create Emp
//------------------------------

app.post('/create-employee', (req, res) => {
    const { username, password, fname, lname, email, contact } = req.body;

    const sql = `
        INSERT INTO users
        (username, password, fname, lname, email, contact, role, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;

    const values = [
        username,
        password,
        fname,
        lname,
        email,
        contact,
        'EMPLOYEE',
        'APPROVED'
    ];

    conn.query(sql, values, (err) => {
        if (err) {
            console.log(err);
            return res.status(500).send("Employee Creation Failed");
        }

        res.status(201).send("Employee Created Successfully");
    });
});




app.listen(9000, () => {
    console.log("Server started at port 9000");
});




