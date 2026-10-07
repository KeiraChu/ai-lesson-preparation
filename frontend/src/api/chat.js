import request from '@/utils/request'

export const gettext = prompt => {
  const res = request.post(
    'text',
    { prompt },
    {
      headers: {
        'Content-Type': 'application/json'
      }
    }
  )

  return res
}
