import { useState } from "react";
import { login } from "../redux/authslice";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

export default function LoginComp() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handelSubmit = (e) => {
    e.preventDefault();

    const reqoptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username,
        password,
      }),
    };

    fetch("http://localhost:9000/login", reqoptions)
      .then((resp) => {
        if (resp.status === 200) {
          return resp.json();
        } else if (resp.status === 404) {
          setMsg("Invalid Username or Password");
          return null;
        }
      })
      .then((data) => {
        if (!data) return;

        dispatch(
          login({
            user: data.user,
            token: data.token,
          })
        );

        const role = data.user.role;

        if (role === "ADMIN") {
          navigate("/admin");
        } 
        else if (role === "HR_HEAD") {
          navigate("/hr");
        } 
        else if (role === "MANAGER") {
          navigate("/manager");
        } 
        else if (role === "EMPLOYEE") {
          navigate("/employee");
        } 
        else {
          navigate("/");
        }
      })
      .catch((err) => {
        console.log(err);
        setMsg("Something went wrong");
      });
  };

  return (
    <>
      <h1>Login Component</h1>

      <form>
        <label>UserName:</label>
        <input
          type="text"
          name="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <br />

        <label>Password:</label>
        <input
          type="password"
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <br />

        <input
          type="submit"
          value="LOGIN"
          onClick={handelSubmit}
        />
      </form>

      <p>{msg}</p>
    </>
  );
}