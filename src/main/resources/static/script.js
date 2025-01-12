const apiUrl = 'http://localhost:8080/api';

async function loadDashboard() {
	const userResponse = await fetch(`${apiUrl}/user`);
	const user = await userResponse.json();
	document.getElementById('user').textContent = `Welcome ${user.username}!`;
		
    const walletResponse = await fetch(`${apiUrl}/wallet`);
    const wallet = await walletResponse.json();
    document.getElementById('wallet').textContent = `${wallet.total} USDT`;
	document.getElementById('walletUsdt').textContent = `USDT: ${wallet.usdt}`;
	document.getElementById('walletBtc').textContent = `BTC: ${wallet.btc}`;
	document.getElementById('walletEth').textContent = `ETH: ${wallet.eth}`;
	
    const historyResponse = await fetch(`${apiUrl}/history`);
    const history = await historyResponse.json();
    const historyTable = document.getElementById('history');
    historyTable.innerHTML = '';
    history.forEach(trade => {
        const row = `<tr>
            <td>${trade.timestamp}</td>
            <td>${trade.crypto_pair}</td>
            <td>${trade.order_type}</td>
            <td>${trade.amount}</td>
            <td>${trade.price}</td>
        </tr>`;
        historyTable.innerHTML += row;
    });
	
	fetchPrices();
}

async function fetchPrices() {
    try {
        const priceResponse = await fetch(`${apiUrl}/prices`);
        const prices = await priceResponse.json();

        document.getElementById('btcPrice').textContent = `BTC/USDT: Sell at ${prices.btcBid}, Buy at ${prices.btcAsk}`;
        document.getElementById('ethPrice').textContent = `ETH/USDT: Sell at ${prices.ethBid}, Buy at ${prices.ethAsk}`;
    } catch (error) {
        console.error("Error fetching prices:", error);
    }
}

setInterval(fetchPrices, 10000); 
setInterval(loadDashboard, 10000); 

async function submitTrade() {
    const pair = document.getElementById('pair').value;
    const type = document.getElementById('type').value;
    const amount = document.getElementById('amount').value;

    const response = await fetch(`${apiUrl}/trade`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ pair, type, amount }),
    });

    if (response.ok) {
        alert('Trade executed successfully!');
        loadDashboard(); 
    } else {
        alert('Trade failed!');
    }
}

loadDashboard();
