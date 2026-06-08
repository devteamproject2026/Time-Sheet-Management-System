const exp= require('express');
const app=exp();
const mysql= require('mysql2');
const cors= require('cors');

//Database Connection 
const conn =mysql.createConnection({
    host:"localhost",
    user:"root",
    password:"ganesh2002",
    database:"CDAC"
})
conn.connect((err)=>{
    if(err){
        console.log("Error connecting to database"+err);
    }
    else{
        console.log("DataBAse connected Successfully");
    }
})


//Middleware
app.use(exp.json());

app.use(cors("*"));


const checkLogin=(req,res,next)=>{
    const{username, password}=req.body;
    if(!username || !password){
        return res.send("Username or Password doesnt match ")
    }
    next();
}

app.get('/users', (req, res) => {
    conn.query("SELECT * FROM users;", (err, result) => {
        if (!err) {
            res.json(result);
        } else {
            console.error("Database error:", err.message);
            
        }
    });
});

app.get('/users/:id',(req,res)=>{
    const id=req.params.id;
    conn.query("select * from users where userid=?",[id],(err,result)=>{
        if(!err){
            res.json(result);
            console.log("Data fetch of id"+id);
        }
        else{
            console.log("Failed to fetch The Data");
        }
    });

})

app.post('/register',(req,res)=>{
    console.log(req.body);
    const {username,password,fname,mname ,lname,email,contact}= req.body
    //const userid=req.body;
    // const username=req.body.us;
    // const password=req.body;
    // const fname=req.body;
    // const mname=req.body;
    // const lname=req.body;
    // const email=req.body;
    // const contact=req.body;
     const sql="Insert into users (username,password,fname,mname,lname,email,contact) VALUES (?, ?, ?, ?, ?, ?, ?)";
     const values=[username, password, fname, mname, lname, email, contact];

    conn.query(sql,values,(err)=>{
        if(!err){
            console.log("register successfully..");
            res.send("Data Entered successfully...");
        }
        else{
            console.log("Fariled to register ..")
            console.log(err.message);
        }
    })
})

app.post('/login', (req, res) => {

    const { username, password } = req.body;

    const sql = "SELECT * FROM users WHERE username=? AND password=?";
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



app.post("/reset", (req, res)=>{
    const {username, password, new_password}=req.body
    conn.query("select * from users where username=? and password=?",[username, password], (err, result)=>{
        if(err){
            console.log(err);
            res.send("Error at server side")
        }else{
            if(result.length>0){
                console.log("Login successful");
                conn.query("update users set password=? where username=?",[new_password, username], (err, data)=>{
                    if(err){
                        console.log(err);
                        res.send("Error at server side")
                    }else{
                        res.send("password updated")
                    }
                })
                
            }else{
                res.send("Wrong uid or password")
            }
            // res.send(result)
        }
    })
})




app.listen(9000, () => {
    console.log("Server started at port 9000");
});