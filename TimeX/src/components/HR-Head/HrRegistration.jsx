import { useState } from "react";

export default function HrRegistration() {

    const [formData, setFormData] = useState({
        username: "",
        password: "",
        fname: "",
        lname: "",
        email: "",
        contact: ""
    });

    const [msg, setMsg] = useState("");

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {

        e.preventDefault();

        const reqOptions = {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        };

        fetch("http://localhost:9000/register-hr", reqOptions)
            .then((resp) => {

                if (resp.status === 201) {
                    setMsg("Registration Request Submitted Successfully");
                } else {
                    setMsg("Registration Failed");
                }

                return resp.text();
            })
            .catch((err) => {
                console.log(err);
                setMsg("Something Went Wrong");
            });
    };

    return (
        <>
            <h1>HR Registration</h1>

            <form onSubmit={handleSubmit}>

                <label>Username:</label>
                <input
                    type="text"
                    name="username"
                    value={formData.username}
                    onChange={handleChange}
                />
                <br />

                <label>Password:</label>
                <input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                />
                <br />

                <label>First Name:</label>
                <input
                    type="text"
                    name="fname"
                    value={formData.fname}
                    onChange={handleChange}
                />
                <br />

                <label>Last Name:</label>
                <input
                    type="text"
                    name="lname"
                    value={formData.lname}
                    onChange={handleChange}
                />
                <br />

                <label>Email:</label>
                <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                />
                <br />

                <label>Contact:</label>
                <input
                    type="text"
                    name="contact"
                    value={formData.contact}
                    onChange={handleChange}
                />
                <br />

                <input
                    type="submit"
                    value="Register"
                />

            </form>

            <p>{msg}</p>
        </>
    );
}