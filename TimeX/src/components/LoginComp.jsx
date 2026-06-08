import { useState } from "react";
import { login } from "../redux/authslice";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

export default function LoginComp() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");
  const dispatch = useDispatch();
  const navigate=useNavigate();

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
        if (resp.status == 200) return resp.json();
        else if (resp.status == 404) {
          setMsg("Invalid username or Password");
          return {};
        }
      })
      .then((data) => {
        //redux state Modify
        //routing to the dashboard
        //console.log(JSON.stringify(data));
        dispatch(login({ user: data.user, token: data.token }));

        const role = data.user.role;

        if (role == 1) {
          navigate("/admin");
        } else if (role == 2) {
          navigate("/user");
        } else {
          navigate("/");
        }
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
          onChange={(e) => {
            setUsername(e.target.value);
          }}
        />
        <br />
        <label>Password:</label>
        <input
          type="text"
          name="password"
          value={password}
          onChange={(e) => {
            setPassword(e.target.value);
          }}
        />
        <br />

        <input type="submit" value="LOGIN" onClick={handelSubmit} />
      </form>

      <p>{msg}</p>
      <p>{username}</p>
      <p>{password}</p>
    </>
  );
}
