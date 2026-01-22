import { useState } from 'react';
import api from '../services/api';
import { useNavigate, Link } from 'react-router-dom';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/auth/login', { username, password });
            localStorage.setItem('token', response.data.token);
            navigate('/dashboard');
            window.location.reload();
        } catch (err) {
            setError('Invalid username or password!');
        }
    };

    return (
        <div className="flex justify-center items-center h-[80vh] bg-gray-50">
            <div className="w-full max-w-sm bg-white p-8 rounded-xl shadow-lg border border-gray-100">
                <h2 className="text-3xl font-extrabold mb-6 text-center text-gray-900">Welcome Back! 👋</h2>
                <p className="text-center text-gray-500 mb-8">Enter your details to access your account.</p>

                {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm text-center border border-red-100">{error}</div>}

                <form onSubmit={handleLogin}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Username</label>
                        <input
                            type="text"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition"
                            placeholder="e.g. trader1"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                        <input
                            type="password"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                    <button type="submit" className="w-full bg-indigo-600 text-white p-3 rounded-lg hover:bg-indigo-700 transition font-bold shadow-md hover:shadow-lg">
                        Login
                    </button>
                </form>

                <div className="mt-6 text-center border-t border-gray-100 pt-4">
                    <p className="text-sm text-gray-600">Don't have an account yet?</p>
                    <Link to="/register" className="text-indigo-600 font-bold hover:text-indigo-800 transition">
                        Create a new account
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;