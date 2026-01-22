import { useState } from 'react';
import api from '../services/api';
import { useNavigate, Link } from 'react-router-dom';

const RegisterPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await api.post('/auth/register', { username, password });
            setSuccess(true);
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed. Try a different username.');
        }
    };

    return (
        <div className="flex justify-center items-center h-screen bg-gray-50">
            <div className="w-full max-w-sm bg-white p-8 rounded-xl shadow-lg border border-gray-100">
                <h2 className="text-3xl font-extrabold mb-6 text-center text-gray-900">Create Account 🚀</h2>

                {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm text-center border border-red-100">{error}</div>}
                {success && <div className="bg-green-100 text-green-700 p-3 rounded-lg mb-4 text-sm text-center border border-green-100">Account created! Redirecting...</div>}

                <form onSubmit={handleRegister}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Username</label>
                        <input
                            type="text"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                        <input
                            type="password"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" disabled={success} className="w-full bg-indigo-600 text-white p-3 rounded-lg hover:bg-indigo-700 transition font-bold disabled:opacity-50 shadow-md">
                        Sign Up
                    </button>
                </form>
                <p className="mt-6 text-center text-sm text-gray-600">
                    Already have an account? <Link to="/login" className="text-indigo-600 font-bold hover:underline">Login here</Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;