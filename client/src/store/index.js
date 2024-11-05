// store.js
import { createStore } from 'vuex'

const store = createStore({
  state: {
    account: JSON.parse(sessionStorage.getItem('account')) || null,
    isAdmin: JSON.parse(sessionStorage.getItem('isAdmin')) || false,
    isArtist: JSON.parse(sessionStorage.getItem('isArtist')) || false
  },
  mutations: {
    setAccount (state, accountData) {
      state.account = accountData
      sessionStorage.setItem('account', JSON.stringify(accountData))
      sessionStorage.setItem('email', accountData.email)
      sessionStorage.setItem('nickname', accountData.nickname)
      sessionStorage.setItem('role', accountData.role)
    },
    setAdminStatus (state, isAdmin) {
      state.isAdmin = isAdmin
      sessionStorage.setItem('isAdmin', JSON.stringify(isAdmin))
    },
    setArtistStatus (state, isArtist) {
      state.isArtist = isArtist
      sessionStorage.setItem('isArtist', JSON.stringify(isArtist))
    },
    clearState (state) {
      state.account = null
      state.isAdmin = false
      state.isArtist = false
      sessionStorage.removeItem('account')
      sessionStorage.removeItem('isAdmin')
      sessionStorage.removeItem('email')
      sessionStorage.removeItem('nickname')
      sessionStorage.removeItem('role')
      sessionStorage.removeItem('isArtist')
    }
  },
  actions: {
    checkRole ({ commit, state }) {
      return new Promise((resolve) => {
        const role = state.account ? state.account.role : null
        const isAdmin = role === 'ADMIN'
        const isArtist = role === 'ARTIST'
        commit('setAdminStatus', isAdmin)
        commit('setArtistStatus', isArtist)
        resolve({ isAdmin, isArtist })
      })
    },
    logout ({ commit }) {
      commit('clearState')
    }
  },
  getters: {
    isAdmin: state => state.isAdmin,
    isArtist: state => state.isArtist,
    account: state => state.account
  }
})

export default store
