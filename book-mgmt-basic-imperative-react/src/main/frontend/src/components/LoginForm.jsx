import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

function LoginForm() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const location = useLocation();
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    navigate('/success');
  };

  const query = new URLSearchParams(location.search);
  const hasError = query.get('error');
  const hasLogout = query.get('logout');

  return (
    <div className="container">
      <div className="row">
        <div className="col-lg-4 offset-lg-4 center-div">
          <h2>Login</h2>

          {hasError && (
            <div className="mt-3 text-danger">Invalid username or password</div>
          )}

          {hasLogout && (
            <div className="mt-3 text-primary">You have been logged out</div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group mt-3">
              <label htmlFor="username">Username</label>
              <input
                type="text"
                className="form-control"
                id="username"
                name="username"
                placeholder="user or admin"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoFocus
              />
            </div>
            <div className="form-group mt-3">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                className="form-control"
                id="password"
                name="password"
                placeholder="SecretPass"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            <button type="submit" className="btn btn-primary mt-3">
              Sign in
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default LoginForm;
