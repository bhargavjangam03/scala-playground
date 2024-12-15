import streamlit as st
import pandas as pd
import requests

# Fetch data from the API
def fetch_aggregated_data(api_url):
    try:
        response = requests.get(api_url)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"❌ Error fetching data: {e}")
        return []

# Main app
def main():
    # App Title with style
    st.markdown(
        """
        <style>
        .title {
            font-size: 32px;
            font-weight: bold;
            color: #4CAF50;
            text-align: center;
        }
        </style>
        """,
        unsafe_allow_html=True,
    )
    st.markdown('<div class="title">Sensor Metrics Viewer</div>', unsafe_allow_html=True)

    # Search section
    st.markdown(
        """
        <style>
        .search-box {
            margin-top: 20px;
        }
        </style>
        """,
        unsafe_allow_html=True,
    )
    search_col1, search_col2 = st.columns([3, 1])
    with search_col1:
        sensor_id = st.text_input(
            "🔍 Search by Sensor ID",
            placeholder="Enter Sensor ID here",
            help="Provide a valid Sensor ID to filter data."
        )
    with search_col2:
        search_button = st.button("🔎 Search", use_container_width=True, disabled=not bool(sensor_id.strip()))

    # Refresh data button
    st.markdown('<div class="search-box"></div>', unsafe_allow_html=True)
    refresh_button = st.button("🔄 Refresh Data", use_container_width=True)

    # Placeholder for the table
    table_placeholder = st.empty()

    # API base URL
    base_api_url = "http://0.0.0.0:8080/api/aggregated-data"

    # Refresh data logic
    if refresh_button:
        data = fetch_aggregated_data(base_api_url)
        if data:
            df = pd.DataFrame(data)
            st.success(f"✅ Fetched {len(df)} records successfully!")
            table_placeholder.table(
                df.style.set_properties(**{'text-align': 'center', 'color': '#FFFFFF', 'background-color': '#000000'})  # Set text color to white, background color to black
                .set_table_styles([
                    {'selector': 'th', 'props': [
                        ('background-color', '#007BFF'),  # Set header background to blue
                        ('color', '#FFFFFF'),            # Set header text color to white
                        ('font-weight', 'bold'),         # Make header text bold
                        ('border', '1px solid #DDDDDD')  # Add border to header cells
                    ]},
                    {'selector': 'td', 'props': [
                        ('border', '1px solid #DDDDDD'),  # Add border to data cells
                        ('padding', '5px'),               # Add padding for better readability
                        ('background-color', '#000000'),  # Set data cell background color to black
                        ('color', '#FFFFFF')              # Set data cell text color to white
                    ]}
                ])
            )

        else:
            st.warning("⚠️ No data available.")

    # Search by Sensor ID logic
    if search_button and sensor_id.strip():
        api_url = f"{base_api_url}/{sensor_id.strip()}"
        data = fetch_aggregated_data(api_url)
        if data:
            df = pd.DataFrame(data)
            st.success(f"✅ Found {len(df)} records for Sensor ID {sensor_id.strip()}.")
            table_placeholder.table(
                df.style.set_properties(**{'text-align': 'center', 'color': '#FFFFFF', 'background-color': '#000000'})  # Set text color to white, background color to black
                .set_table_styles([
                    {'selector': 'th', 'props': [
                        ('background-color', '#007BFF'),  # Set header background to blue
                        ('color', '#FFFFFF'),            # Set header text color to white
                        ('font-weight', 'bold'),         # Make header text bold
                        ('border', '1px solid #DDDDDD')  # Add border to header cells
                    ]},
                    {'selector': 'td', 'props': [
                        ('border', '1px solid #DDDDDD'),  # Add border to data cells
                        ('padding', '5px'),               # Add padding for better readability
                        ('background-color', '#000000'),  # Set data cell background color to black
                        ('color', '#FFFFFF')              # Set data cell text color to white
                    ]}
                ])
            )

        else:
            st.warning(f"⚠️ No data found for Sensor ID {sensor_id.strip()}.")

    # Add footer with style
    st.markdown(
        """
        <style>
        footer {
            position: fixed;
            bottom: 0;
            width: 100%;
            background-color: #f4f4f4;
            text-align: center;
            padding: 10px;
        }
        </style>
        <footer>
        📊 Built with <span style="color: #FF4B4B;">❤</span> using Streamlit
        </footer>
        """,
        unsafe_allow_html=True,
    )

if __name__ == "__main__":
    main()
