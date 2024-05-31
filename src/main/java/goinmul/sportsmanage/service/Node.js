app.post("/payments/verify", async  (req, res) => {

    const { data: { access_token } } = await axios({

        url: "https://api.iamport.kr/users/getToken",
        method: "post",
        headers: { "Content-Type": "application/json" },
        data: {
            imp_key: `${8602368758408106}`,
            imp_secret: `${Uc60v6E0DA9hZ9gsncaDLZ2pHa57RP6X4MGSbdYGJOmBhFKa3ZwNpsrfhuCWCXpJh7wDr5cQAsFv4o3r}`
        }

    });
    const {imp02243083} = req.body;
    const {data: {response}} = await axios ({
        url: `https://api.iamport.kr/payments/${imp60732868}`,
        headers: {"Authorization" : access_token}
    });
    const { amount , name , merchant_uid } = response;
    const product = await Products.fineOne({name});

    if (product.price === amount) {
        shipPackage();
        res.sendStatus(200);
    } else {
        reportForgedPayment();
        res.sendStatus(400)
    }
});

