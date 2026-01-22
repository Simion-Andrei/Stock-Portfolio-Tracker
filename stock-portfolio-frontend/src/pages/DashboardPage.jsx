import { useEffect, useState } from 'react';
import api from '../services/api';
import Navbar from '../components/Navbar';

const DashboardPage = () => {
    // --- STATE ---
    const [portfolio, setPortfolio] = useState(null);
    const [stocks, setStocks] = useState([]);
    const [loading, setLoading] = useState(true);

    // Buy Form State
    const [selectedTicker, setSelectedTicker] = useState('');
    const [buyQuantity, setBuyQuantity] = useState(1);
    const [customBuyPrice, setCustomBuyPrice] = useState('');

    // Sell Modal State
    const [isSellModalOpen, setIsSellModalOpen] = useState(false);
    const [stockToSell, setStockToSell] = useState(null);
    const [sellQuantity, setSellQuantity] = useState(1);

    // --- FETCH DATA ---
    const fetchData = async () => {
        try {
            const portfolioRes = await api.get('/portfolio');
            const stocksRes = await api.get('/portfolio/stocks');

            setPortfolio(portfolioRes.data);
            setStocks(stocksRes.data);

            if(stocksRes.data.length > 0 && !selectedTicker) {
                setSelectedTicker(stocksRes.data[0].ticker);
            }
            setLoading(false);
        } catch (error) {
            console.error("Error loading data", error);
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    // --- HANDLERS ---

    // 1. BUY
    const handleBuy = async (e) => {
        e.preventDefault();
        try {
            await api.post('/portfolio', {
                ticker: selectedTicker,
                quantity: parseInt(buyQuantity),
                buyPrice: customBuyPrice ? parseFloat(customBuyPrice) : null
            });
            alert('Transaction successful!');
            fetchData();
            setBuyQuantity(1);
            setCustomBuyPrice('');
        } catch (err) {
            alert('Purchase failed!');
        }
    };

    // 2. OPEN SELL MODAL
    const openSellModal = (ticker) => {
        setStockToSell(ticker);
        setSellQuantity(1);
        setIsSellModalOpen(true);
    };

    // 3. CONFIRM SELL
    const handleConfirmSell = async () => {
        if (!stockToSell) return;

        try {
            await api.post('/portfolio/sell', {
                ticker: stockToSell,
                quantity: parseInt(sellQuantity)
            });
            setIsSellModalOpen(false);
            fetchData();
        } catch (err) {
            alert(err.response?.data?.message || 'Sale failed');
        }
    };

    if (loading) return <div className="text-center mt-20 text-xl font-bold text-gray-600">Loading portfolio...</div>;

    return (
        <div className="min-h-screen bg-gray-50 pb-10 relative">
            <Navbar />

            <div className="container mx-auto px-4 py-8">
                {/* --- STATS CARDS --- */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 transition hover:shadow-md">
                        <p className="text-gray-500 text-xs font-bold uppercase tracking-wider">Total Value</p>
                        <p className="text-3xl font-extrabold text-gray-900 mt-1">
                            ${portfolio?.totalPortfolioValue?.toFixed(2) || '0.00'}
                        </p>
                    </div>

                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 transition hover:shadow-md">
                        <p className="text-gray-500 text-xs font-bold uppercase tracking-wider">Profit / Loss</p>
                        <p className={`text-3xl font-extrabold mt-1 ${portfolio?.totalProfitLoss >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                            {portfolio?.totalProfitLoss >= 0 ? '+' : ''}
                            {portfolio?.totalProfitLoss?.toFixed(2) || '0.00'}
                        </p>
                    </div>

                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 transition hover:shadow-md">
                        <p className="text-gray-500 text-xs font-bold uppercase tracking-wider">Return</p>
                        <p className={`text-3xl font-extrabold mt-1 ${portfolio?.totalReturnPercent >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                            {portfolio?.totalReturnPercent?.toFixed(2) || '0.00'}%
                        </p>
                    </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* --- TABLE (Left) --- */}
                    <div className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden h-fit">
                        <div className="p-6 border-b border-gray-100 bg-gray-50/50 flex justify-between items-center">
                            <h3 className="font-bold text-gray-800 text-lg">Active Portfolio</h3>
                            <span className="bg-indigo-100 text-indigo-800 text-xs font-bold px-2.5 py-0.5 rounded-full">
                                {portfolio?.holdings?.length || 0} Positions
                            </span>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 text-gray-500 text-xs uppercase font-semibold">
                                <tr>
                                    <th className="p-4">Ticker</th>
                                    <th className="p-4">Qty</th>
                                    <th className="p-4">Avg. Cost</th>
                                    <th className="p-4">Market Price</th>
                                    <th className="p-4">Value</th>
                                    <th className="p-4">P/L</th>
                                    <th className="p-4 text-center">Actions</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                {portfolio?.holdings?.map((h) => (
                                    <tr key={h.ticker} className="hover:bg-gray-50 transition-colors group">
                                        <td className="p-4 font-bold text-gray-700">{h.ticker}</td>
                                        <td className="p-4">{h.quantity}</td>
                                        <td className="p-4 text-gray-500">${h.buyPrice.toFixed(2)}</td>
                                        <td className="p-4 text-gray-500">${h.currentPrice.toFixed(2)}</td>
                                        <td className="p-4 font-bold text-gray-800">${h.totalValue.toFixed(2)}</td>
                                        <td className={`p-4 font-bold ${h.profitLoss >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                                            {h.profitLoss.toFixed(2)}
                                        </td>
                                        <td className="p-4 text-center">
                                            <button
                                                onClick={() => openSellModal(h.ticker)}
                                                className="bg-red-50 text-red-600 border border-red-100 hover:bg-red-600 hover:text-white px-3 py-1.5 rounded-md text-sm font-medium transition shadow-sm"
                                            >
                                                Sell
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                {portfolio?.holdings?.length === 0 && (
                                    <tr>
                                        <td colSpan="7" className="p-12 text-center text-gray-400 italic">
                                            No assets yet. Use the form on the right to start investing!
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    {/* --- BUY FORM (Right) --- */}
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-fit sticky top-6">
                        <h3 className="font-bold text-gray-800 mb-6 text-lg border-b pb-2">Buy Stocks</h3>
                        <form onSubmit={handleBuy} className="space-y-5">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Company</label>
                                <select
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none bg-white transition"
                                    value={selectedTicker}
                                    onChange={(e) => setSelectedTicker(e.target.value)}
                                >
                                    {stocks.map(s => (
                                        <option key={s.ticker} value={s.ticker}>
                                            {s.ticker} - {s.name} (${s.currentPrice})
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Quantity</label>
                                    <input
                                        type="number"
                                        min="1"
                                        className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition"
                                        value={buyQuantity}
                                        onChange={(e) => setBuyQuantity(e.target.value)}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Price (Optional)</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        placeholder="Market"
                                        className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition"
                                        value={customBuyPrice}
                                        onChange={(e) => setCustomBuyPrice(e.target.value)}
                                    />
                                </div>
                            </div>

                            <button type="submit" className="w-full bg-indigo-600 text-white py-3 rounded-lg font-bold hover:bg-indigo-700 transition shadow-md hover:shadow-lg mt-2">
                                Confirm Investment
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            {/* --- SELL MODAL --- */}
            {isSellModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
                    <div className="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden transform transition-all scale-100">
                        {/* Header Modal */}
                        <div className="bg-gray-50 px-6 py-4 border-b border-gray-100 flex justify-between items-center">
                            <h3 className="text-lg font-bold text-gray-800">Sell {stockToSell}</h3>
                            <button onClick={() => setIsSellModalOpen(false)} className="text-gray-400 hover:text-gray-600 transition">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>

                        {/* Body Modal */}
                        <div className="p-6">
                            <p className="text-gray-600 mb-4 text-sm">
                                How many <span className="font-bold text-gray-800">{stockToSell}</span> shares do you want to sell?
                                <br/>These will be sold at the current market price.
                            </p>

                            <label className="block text-sm font-medium text-gray-700 mb-1">Quantity to sell</label>
                            <input
                                type="number"
                                min="1"
                                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none transition text-lg font-medium"
                                value={sellQuantity}
                                onChange={(e) => setSellQuantity(e.target.value)}
                            />
                        </div>

                        {/* Footer Modal */}
                        <div className="bg-gray-50 px-6 py-4 flex justify-end gap-3">
                            <button
                                onClick={() => setIsSellModalOpen(false)}
                                className="px-4 py-2 text-gray-700 font-medium hover:bg-gray-200 rounded-lg transition"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleConfirmSell}
                                className="px-4 py-2 bg-red-600 text-white font-bold rounded-lg hover:bg-red-700 shadow-md hover:shadow-lg transition"
                            >
                                Confirm Sale
                            </button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
};

export default DashboardPage;